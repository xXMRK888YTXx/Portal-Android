package com.xxmrk888ytxx.settingsscreen.model

sealed interface BottomSheetState {
    data object None : BottomSheetState
    data class ConfirmSecurityChangesDialog(
        val isForEnablingSetting: Boolean,
        val actionAfterConfirm: () -> Unit,
    ) : BottomSheetState
}