package com.xxmrk888ytxx.mainscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import com.xxmrk888ytxx.mainscreen.contract.ProvideSavedDevices
import com.xxmrk888ytxx.mainscreen.contract.SendUnlockRequestContract
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
        val createShortcutDialogState =
            createShortcutDialogState.value as? CreateShortcutDialogState.Showed ?: return
        val shortcutOption = ShortcutOption(
            createShortcutDialogState.clientId,
            createShortcutDialogState.isRequiredBiometricUnlock
        )
        handleEvent(MainScreenEvent.DismissCreateShortcutModelDialog)
        // TODO
    }

    private fun updateCreateShortcutDialogState(onUpdate: (CreateShortcutDialogState.Showed) -> CreateShortcutDialogState.Showed) {
        createShortcutDialogState.update {
            if (it !is CreateShortcutDialogState.Showed) return@update it
            onUpdate(it)
        }
    }

    private fun showCreateShortcutDialog(device: Device) {
        createShortcutDialogState.value = CreateShortcutDialogState.Showed(device.deviceId)
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