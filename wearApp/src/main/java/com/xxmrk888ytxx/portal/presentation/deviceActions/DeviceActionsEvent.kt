package com.xxmrk888ytxx.portal.presentation.deviceActions

sealed interface DeviceActionsEvent {
    data object NavigateBack : DeviceActionsEvent
    data class Unlock(val clientId: String) : DeviceActionsEvent
    data class WakeOnLanUnlock(val clientId: String) : DeviceActionsEvent
}
