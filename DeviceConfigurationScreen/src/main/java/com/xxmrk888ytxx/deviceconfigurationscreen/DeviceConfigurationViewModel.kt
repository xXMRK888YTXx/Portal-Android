package com.xxmrk888ytxx.deviceconfigurationscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.mvi.DefaultSideEffect
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.RemoveDeviceContract
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationUiEvent
import com.xxmrk888ytxx.deviceconfigurationscreen.model.ScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class DeviceConfigurationViewModel @AssistedInject internal constructor(
    @Assisted private val deviceId: String,
    private val provideDeviceInfoContract: ProvideDeviceInfoContract,
    private val removeDeviceContract: RemoveDeviceContract,
) :
    SideEffectPortalViewModel<ScreenState, DeviceConfigurationUiEvent>(ScreenState.Loading) {

    override fun handleEvent(event: DeviceConfigurationUiEvent) {
        when(event) {
            DeviceConfigurationUiEvent.NavigateBack -> sendNavigateUpSideEffect()
            DeviceConfigurationUiEvent.RemoveDevice -> removeDevice()
        }
    }

    private fun removeDevice() {
        val device = _state.value as? ScreenState.DeviceInfo ?: return
        _state.value = ScreenState.Loading
        viewModelScope.launch {
            removeDeviceContract.removeDevice(device.device.deviceId)
        }.invokeOnCompletion { sendNavigateUpSideEffect() }
    }

    init {
        viewModelScope.launch {
            provideDeviceInfoContract.provideDeviceInfo(deviceId)
                .onSuccess { _state.value = ScreenState.DeviceInfo(it) }
                .onFailure {
                    sideEffect.emit(DefaultSideEffect.ShowToast(uiText(R.string.device_not_found)))
                    sideEffect.emit(DefaultSideEffect.NavigationBack)
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted deviceId: String,
        ): DeviceConfigurationViewModel
    }
}