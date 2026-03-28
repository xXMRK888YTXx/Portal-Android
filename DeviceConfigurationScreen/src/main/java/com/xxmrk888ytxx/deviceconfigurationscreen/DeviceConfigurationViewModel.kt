package com.xxmrk888ytxx.deviceconfigurationscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.mvi.DefaultSideEffect
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ChangeDeviceSettingsContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.RemoveDeviceContract
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeviceConfigurationViewModel @AssistedInject internal constructor(
    @Assisted private val deviceId: String,
    private val provideDeviceInfoContract: ProvideDeviceInfoContract,
    private val removeDeviceContract: RemoveDeviceContract,
    private val changeDeviceSettingsContract: ChangeDeviceSettingsContract,
) : SideEffectPortalViewModel<ScreenState, DeviceConfigurationUiEvent>(ScreenState.Loading) {

    private val isSettingsUpdateInProgress = MutableStateFlow(false)
    private val isDeletionInProgress = MutableStateFlow(false)


    private val observeDeviceJob = viewModelScope.launch {
        provideDeviceInfoContract.provideDeviceInfo(deviceId)
            .catch {
                sideEffect.emit(DefaultSideEffect.ShowToast(uiText(R.string.device_not_found)))
                sideEffect.emit(DefaultSideEffect.NavigationBack)
            }
            .onEach { fastDebugLog(it) }
            .collect { device ->
                _state.value = ScreenState.DeviceInfo(device)
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

            is DeviceConfigurationUiEvent.OnDeviceNameChanged -> changeDeviceName(event.newName)
            is DeviceConfigurationUiEvent.HideRemoveDialog -> hideDeletionDialog()
            is DeviceConfigurationUiEvent.ShowRemoveDialog -> showDeletionDialog()
            is DeviceConfigurationUiEvent.OpenBluetoothSettings -> sideEffect.tryEmit(
                DeviceConfigurationScreenSideEffect.OpenBluetoothSettings
            )
        }
    }

    private fun changeDeviceName(newName: String) = viewModelScope.launch {
        changeDeviceSettingsContract.updateDeviceName(newName, deviceId)
    }

    private fun changeUnlockOnlyWhenScreenUnlockedState(newValue: Boolean) = viewModelScope.launch {
        changeDeviceSettingsContract.updateUnlockOnlyWhenScreenUnlockedState(deviceId, newValue)
    }

    private fun changeUnlockMethodState(newMethod: UnlockMethod) = viewModelScope.launch {
        changeDeviceSettingsContract.updateUnlockMethodState(deviceId, newMethod)
    }

    private fun changeHostState(newIp: String) = viewModelScope.launch {
        changeDeviceSettingsContract.updateHost(newIp, deviceId)
    }

    private fun changeSearchIpDynamicallyState(newValue: Boolean) = withLoading {
        changeDeviceSettingsContract.updateSearchIpDynamicallyState(deviceId, newValue)
    }

    private fun changeAwaitUnlockRequestsState(newValue: Boolean) = withLoading {
        changeDeviceSettingsContract.updateAwaitUnlockRequestsState(deviceId, newValue)
    }

    private fun showDeletionDialog() {
        _state.update {
            (it as? ScreenState.DeviceInfo)?.copy(bottomSheetDialogState = BottomSheetDialogState.DeleteDevice)
                ?: it
        }
    }

    private fun hideDeletionDialog() {
        _state.update {
            (it as? ScreenState.DeviceInfo)?.copy(bottomSheetDialogState = BottomSheetDialogState.None)
                ?: it
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
            removeDeviceContract.removeDevice(device.device.deviceId)
        }.invokeOnCompletion { sendNavigateUpSideEffect() }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted deviceId: String,
        ): DeviceConfigurationViewModel
    }
}