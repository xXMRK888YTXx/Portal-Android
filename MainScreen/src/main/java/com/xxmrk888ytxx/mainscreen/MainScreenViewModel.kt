package com.xxmrk888ytxx.mainscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import com.xxmrk888ytxx.mainscreen.contract.CreateShortcutContract
import com.xxmrk888ytxx.mainscreen.contract.ProvideSavedDevices
import com.xxmrk888ytxx.mainscreen.contract.SendUnlockRequestContract
import com.xxmrk888ytxx.mainscreen.exception.LauncherNotSupportShortcutException
import com.xxmrk888ytxx.mainscreen.model.CreateShortcutDialogState
import com.xxmrk888ytxx.mainscreen.model.Device
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import com.xxmrk888ytxx.mainscreen.model.ShortcutOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val provideSavedDevices: ProvideSavedDevices,
    private val unlockRequestContract: SendUnlockRequestContract,
    private val createShortcutContract: CreateShortcutContract
) : SideEffectPortalViewModel<ScreenState, MainScreenEvent>(ScreenState()) {

    private val isLoading = MutableStateFlow(false)
    private val createShortcutDialogState =
        MutableStateFlow<CreateShortcutDialogState>(CreateShortcutDialogState.Hidden)


    override val state: StateFlow<ScreenState> =
        combine(
            provideSavedDevices.devices,
            isLoading,
            createShortcutDialogState
        ) { deviceList, isLoading, createShortcutDialogState ->
            ScreenState(deviceList, isLoading, createShortcutDialogState)
        }.stateWhileSubscribed()


    override fun handleEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.AddNewDevice -> sendNavigationAction { fromMainScreenToAddNewDeviceScreen() }
            is MainScreenEvent.SendUnlockRequest -> sendUnlockRequest(event.device)
            is MainScreenEvent.ToDeviceDetailsScreen -> sendNavigationAction {
                fromMainScreenToDeviceConfigurationScreen(
                    event.deviceId
                )
            }

            is MainScreenEvent.ShowCreateShortcutModelDialog -> showCreateShortcutDialog(event.device)
            is MainScreenEvent.DismissCreateShortcutModelDialog -> hideCreateShortcutDialog()
            is MainScreenEvent.OnIsRequiredBiometricUnlockStateChanged -> updateCreateShortcutDialogState {
                it.copy(
                    isRequiredBiometricUnlock = event.isRequiredBiometricUnlock
                )
            }

            MainScreenEvent.CreateShortcut -> createShortcut()
        }
    }

    private fun createShortcut() {
        if (isLoading.value) return
        val createShortcutDialogState =
            createShortcutDialogState.value as? CreateShortcutDialogState.Showed ?: return
        val shortcutOption = ShortcutOption(
            createShortcutDialogState.device,
            createShortcutDialogState.isRequiredBiometricUnlock
        )
        handleEvent(MainScreenEvent.DismissCreateShortcutModelDialog)
        isLoading.update { true }
        viewModelScope.launch {
            createShortcutContract.createShortcutContract(shortcutOption)
                .onSuccess {  }
                .onFailure { error ->
                    fastDebugLog(error)
                    val errorMessage = when(error) {
                        is LauncherNotSupportShortcutException -> {
                           uiText("Your home screen launcher doesn't support shortcuts.")
                        }
                        else -> uiText("Failed to create shortcut. Please try again.")
                    }
                    sendToastSideEffect(errorMessage)
                }
        }.invokeOnCompletion { isLoading.update { false } }
    }

    private fun updateCreateShortcutDialogState(onUpdate: (CreateShortcutDialogState.Showed) -> CreateShortcutDialogState.Showed) {
        createShortcutDialogState.update {
            if (it !is CreateShortcutDialogState.Showed) return@update it
            onUpdate(it)
        }
    }

    private fun showCreateShortcutDialog(device: Device) {
        createShortcutDialogState.value = CreateShortcutDialogState.Showed(device)
    }

    private fun hideCreateShortcutDialog() {
        createShortcutDialogState.value = CreateShortcutDialogState.Hidden
    }

    private fun sendUnlockRequest(device: Device) {
        if (isLoading.value) return
        isLoading.value = true
        viewModelScope.launch {
            unlockRequestContract.unlock(device)
                .onSuccess { sendToastSideEffect(uiText(R.string.device_unlocked)) }
                .onFailure { sendToastSideEffect(uiText(R.string.failed_to_unlock_device)) }
        }.invokeOnCompletion { isLoading.value = false }
    }
}