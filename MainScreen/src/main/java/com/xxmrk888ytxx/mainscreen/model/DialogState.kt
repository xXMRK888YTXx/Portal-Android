package com.xxmrk888ytxx.mainscreen.model

import androidx.compose.ui.text.input.TextFieldValue

sealed interface DialogState {
    object Hidden : DialogState

    data class EnterMacAddressDialog(
        val device: Device,
        val enteredMac: String = "",
        val isValidateMacAddress: Boolean = false
    ) : DialogState

    data class ShortcutDialog(
        val device: Device,
        val isRequiredBiometricUnlock: Boolean = true
    ) : DialogState
}

