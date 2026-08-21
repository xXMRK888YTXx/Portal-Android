package com.xxmrk888ytxx.deviceconfigurationscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.formatToMacAddress
import com.xxmrk888ytxx.coreandroid.mvi.DefaultSideEffect
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ChangeDeviceSettingsContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ChangeMacAddressContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.RemoveDeviceContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.UnsafeMethodAvailableStateProvider
import com.xxmrk888ytxx.deviceconfigurationscreen.model.BottomSheetDialogState
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationScreenSideEffect
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationUiEvent
import com.xxmrk888ytxx.deviceconfigurationscreen.model.ScreenState
import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DeviceConfigurationViewModel @AssistedInject internal constructor(
    @Assisted private val clientId: String,
    private val provideDeviceInfoContract: ProvideDeviceInfoContract,
    private val removeDeviceContract: RemoveDeviceContract,
    private val changeDeviceSettingsContract: ChangeDeviceSettingsContract,
    private val unsafeMethodAvailableStateProvider: UnsafeMethodAvailableStateProvider,
    private val changeMacAddressContract: ChangeMacAddressContract
) : SideEffectPortalViewModel<ScreenState, DeviceConfigurationUiEvent>(ScreenState.Loading) {

    private val isSettingsUpdateInProgress = MutableStateFlow(false)
    private val isDeletionInProgress = MutableStateFlow(false)

    private val updateStateMutex = Mutex()

    private val observeDeviceJob = viewModelScope.launch {
        provideDeviceInfoContract.provideDeviceInfo(clientId)
            .catch {
                sideEffect.emit(DefaultSideEffect.ShowToast(uiText(R.string.device_not_found)))
                sideEffect.emit(DefaultSideEffect.NavigationBack)
            }
            .onEach { fastDebugLog(it) }
            .collect { device ->
                updateStateMutex.withLock {
                    _state.update {
                        (it as? ScreenState.DeviceInfo)?.copy(device = device)
                            ?: ScreenState.DeviceInfo(device)
                    }
                }

            }
    }

    private val observeUnsafeMethodAvailableState = viewModelScope.launch {
        unsafeMethodAvailableStateProvider
            .isDisabled
            .distinctUntilChanged()
            .collect { isDisabled ->
                _state.first { it is ScreenState.DeviceInfo }
                updateStateMutex.withLock {
                    _state.update { state ->
                        (state as? ScreenState.DeviceInfo)?.copy(
                            isUnsafeUnlockMethodsDisabled = isDisabled
                        ) ?: state
                    }
                }
            }
    }

    override fun handleEvent(event: DeviceConfigurationUiEvent) {
        when (event) {
            DeviceConfigurationUiEvent.NavigateBack -> sendNavigateUpSideEffect()
            DeviceConfigurationUiEvent.RemoveDevice -> removeDevice()
            is DeviceConfigurationUiEvent.OnAwaitUnlockChanged -> changeAwaitUnlockRequestsState(
                event.newValue
            )

            is DeviceConfigurationUiEvent.OnSearchIpDynamicallyChanged -> changeSearchIpDynamicallyState(
                event.newValue
            )

            is DeviceConfigurationUiEvent.OnHostChanged -> changeHostState(event.newIp)
            is DeviceConfigurationUiEvent.OnUnlockMethodChanged -> changeUnlockMethodState(event.newMethod)
            is DeviceConfigurationUiEvent.OnUnlockOnlyWhenScreenUnlockedChanged -> changeUnlockOnlyWhenScreenUnlockedState(
                event.newValue
            )

            is DeviceConfigurationUiEvent.OnForwardUnlockRequestsToWearChanged -> changeForwardUnlockRequestsToWearState(
                event.newValue
            )

            is DeviceConfigurationUiEvent.OnDeviceNameChanged -> changeDeviceName(event.newName)
            is DeviceConfigurationUiEvent.HideRemoveDialog -> hideDeletionDialog()
            is DeviceConfigurationUiEvent.ShowRemoveDialog -> showDeletionDialog()
            is DeviceConfigurationUiEvent.OpenBluetoothSettings -> sideEffect.tryEmit(
                DeviceConfigurationScreenSideEffect.OpenBluetoothSettings
            )

            is DeviceConfigurationUiEvent.OnWakeOnLanMacAddressChanged -> updateWOLMacAddress(event.newMac)
        }
    }

    private fun updateWOLMacAddress(newMac: String) = withLoading {
        val formattedMac = newMac.formatToMacAddress() ?: return@withLoading
        changeMacAddressContract.updateWakeOnLanMacAddress(clientId, formattedMac)
    }

    private fun changeDeviceName(newName: String) = viewModelScope.launch {
        changeDeviceSettingsContract.updateDeviceName(newName, clientId)
    }

    private fun changeUnlockOnlyWhenScreenUnlockedState(newValue: Boolean) = viewModelScope.launch {
        changeDeviceSettingsContract.updateUnlockOnlyWhenScreenUnlockedState(clientId, newValue)
    }

    private fun changeForwardUnlockRequestsToWearState(newValue: Boolean) = withLoading {
        changeDeviceSettingsContract.updateForwardUnlockRequestsToWearState(clientId, newValue)
    }

    private fun changeUnlockMethodState(newMethod: UnlockMethod) = viewModelScope.launch {
        changeDeviceSettingsContract.updateUnlockMethodState(clientId, newMethod)
    }

    private fun changeHostState(newIp: String) = viewModelScope.launch {
        changeDeviceSettingsContract.updateHost(newIp, clientId)
    }

    private fun changeSearchIpDynamicallyState(newValue: Boolean) = withLoading {
        changeDeviceSettingsContract.updateSearchIpDynamicallyState(clientId, newValue)
    }

    private fun changeAwaitUnlockRequestsState(newValue: Boolean) = withLoading {
        changeDeviceSettingsContract.updateAwaitUnlockRequestsState(clientId, newValue)
    }

    private fun showDeletionDialog() = viewModelScope.launch {
        updateStateMutex.withLock {
            _state.update {
                (it as? ScreenState.DeviceInfo)?.copy(bottomSheetDialogState = BottomSheetDialogState.DeleteDevice)
                    ?: it
            }
        }
    }

    private fun hideDeletionDialog() = viewModelScope.launch {
        updateStateMutex.withLock {
            _state.update {
                (it as? ScreenState.DeviceInfo)?.copy(bottomSheetDialogState = BottomSheetDialogState.None)
                    ?: it
            }
        }
    }

    private fun withLoading(block: suspend () -> Unit) {
        if (isSettingsUpdateInProgress.value) return
        isSettingsUpdateInProgress.value = true
        viewModelScope.launch {
            block()
        }.invokeOnCompletion { isSettingsUpdateInProgress.value = false }
    }

    private fun removeDevice() {
        if (isDeletionInProgress.value) return
        isDeletionInProgress.value = true
        isSettingsUpdateInProgress.value = true
        val device = _state.value as? ScreenState.DeviceInfo ?: return
        viewModelScope.launch {
            observeDeviceJob.cancelAndJoin()
            _state.value = ScreenState.Loading
            removeDeviceContract.removeDevice(device.device.clientId)
        }.invokeOnCompletion { sendNavigateUpSideEffect() }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted deviceId: String,
        ): DeviceConfigurationViewModel
    }
}
