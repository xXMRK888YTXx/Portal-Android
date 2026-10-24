package com.xxmrk888ytxx.settingsscreen.model

import androidx.compose.ui.graphics.Color
import com.xxmrk888ytxx.corecompose.theme.AppSeedColors

sealed interface BottomSheetState {
    data object None : BottomSheetState
    data class ConfirmSecurityChangesDialog(
        val isForEnablingSetting: Boolean,
        val actionAfterConfirm: () -> Unit,
    ) : BottomSheetState

    data class SelectThemeDialog(
        val selectedThemeColor: Color? = AppSeedColors.RandomColor
    ) : BottomSheetState

    data class SelectAppIconDialog(
        val currentIconId: String = AppIcon.DEFAULT.id
    ) : BottomSheetState

    data class SelectUnlockScreenStyleDialog(
        val currentStyleId: String = UnlockScreenStyle.FULLSCREEN.id
    ) : BottomSheetState
}
