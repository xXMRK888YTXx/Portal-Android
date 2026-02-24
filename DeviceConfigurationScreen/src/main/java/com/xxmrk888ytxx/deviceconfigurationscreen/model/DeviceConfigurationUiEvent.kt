package com.xxmrk888ytxx.deviceconfigurationscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface DeviceConfigurationUiEvent : UiEvent {
    data class OnAwaitUnlockChanged(val newValue: Boolean) : DeviceConfigurationUiEvent
    data object NavigateBack : DeviceConfigurationUiEvent
    data object RemoveDevice : DeviceConfigurationUiEvent
}