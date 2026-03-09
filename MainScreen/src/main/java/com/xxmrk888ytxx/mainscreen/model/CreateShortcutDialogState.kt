package com.xxmrk888ytxx.mainscreen.model

sealed interface CreateShortcutDialogState {
    object Hidden : CreateShortcutDialogState
    data class Showed(
        val clientId: String,
        val isRequiredBiometricUnlock: Boolean = true
    ) : CreateShortcutDialogState
}

