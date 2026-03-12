package com.xxmrk888ytxx.deviceconfigurationscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.mvi.DefaultSideEffect
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ChangeDeviceSettingsContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.RemoveDeviceContract
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationUiEvent
import com.xxmrk888ytxx.deviceconfigurationscreen.model.ScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DeviceConfigurationViewModel @AssistedInject internal constructor(
    @Assisted private val deviceId: String,
    private val provideDeviceInfoContract: ProvideDeviceInfoContract,
    private val removeDeviceContract: RemoveDeviceContract,
    private val changeDeviceSettingsContract: ChangeDeviceSettingsContract
) :
    SideEffectPortalViewModel<ScreenState, DeviceConfigurationUiEvent>(ScreenState.Loading) {

    private var isSettingsUpdateInProgress = false

    private val observeDeviceJob = viewModelScope.launch {
        provideDeviceInfoContract.provideDeviceInfo(deviceId)
            .catch {
                sideEffect.emit(DefaultSideEffect.ShowToast(uiText(R.string.device_not_found)))
                sideEffect.emit(DefaultSideEffect.NavigationBack)
            }
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
        }
    }

    private fun changeSearchIpDynamicallyState(newValue: Boolean) = withLoading {
        changeDeviceSettingsContract.updateSearchIpDynamicallyState(deviceId, newValue)
    }

    private fun changeAwaitUnlockRequestsState(newValue: Boolean) = withLoading {
        changeDeviceSettingsContract.updateAwaitUnlockRequestsState(deviceId, newValue)
    }

    private fun withLoading(block: suspend () -> Unit) {
        if (isSettingsUpdateInProgress) return
        isSettingsUpdateInProgress = true
        viewModelScope.launch {
            block()
        }.invokeOnCompletion { isSettingsUpdateInProgress = false }
    }

    private fun removeDevice() {
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