package com.xxmrk888ytxx.deviceconfigurationscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface DeviceConfigurationUiEvent : UiEvent {
    data object NavigateBack : DeviceConfigurationUiEvent
    data object RemoveDevice : DeviceConfigurationUiEvent
}