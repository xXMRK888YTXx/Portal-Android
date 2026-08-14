package com.xxmrk888ytxx.deviceconfigurationscreen.model

sealed interface BottomSheetDialogState {
    data object None: BottomSheetDialogState
    data object DeleteDevice: BottomSheetDialogState
}