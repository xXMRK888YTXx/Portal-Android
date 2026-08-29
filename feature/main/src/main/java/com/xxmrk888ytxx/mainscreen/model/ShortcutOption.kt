package com.xxmrk888ytxx.mainscreen.model

data class ShortcutOption(
    val device: Device,
    val isRequiredBiometricUnlock: Boolean,
    val isSendWOLRequest: Boolean,
)
