package com.xxmrk888ytxx.mainscreen.model

sealed interface CreateShortcutDialogState {
    object Hidden : CreateShortcutDialogState
    data class Showed(
        val device: Device,
        val isRequiredBiometricUnlock: Boolean = true
    ) : CreateShortcutDialogState
}

