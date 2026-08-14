package com.xxmrk888ytxx.portal.presentation.deviceActions

/**
 * One-off effects emitted after a device command is handled.
 */
sealed interface DeviceActionsSideEffect {
    data object NavigateBack : DeviceActionsSideEffect
    data object ShowCommandSent : DeviceActionsSideEffect
    data object ShowCommandError : DeviceActionsSideEffect
}
