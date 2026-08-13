package com.xxmrk888ytxx.portal.presentation.deviceActions

sealed interface DeviceActionsSideEffect {
    data object NavigateBack : DeviceActionsSideEffect
    data object ShowCommandSent : DeviceActionsSideEffect
    data object ShowCommandError : DeviceActionsSideEffect
}
