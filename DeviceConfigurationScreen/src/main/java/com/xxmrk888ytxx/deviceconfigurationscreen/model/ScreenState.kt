package com.xxmrk888ytxx.deviceconfigurationscreen.model


sealed interface ScreenState {
    data object Loading : ScreenState
    data class DeviceInfo(
        val device: Device,
        val bottomSheetDialogState: BottomSheetDialogState = BottomSheetDialogState.None,
        val isUnsafeUnlockMethodsDisabled: Boolean = false
    ) : ScreenState
}
