package com.xxmrk888ytxx.deviceconfigurationscreen.component

fun String.isValidMacInput(): Boolean {
    val cleanInput = this.replace(":", "")
    val allowedChars = "0123456789abcdefABCDEF"
    return cleanInput.all { it in allowedChars } && cleanInput.length <= 12
}

fun String.isMacComplete(): Boolean {
    return this.replace(":", "").length == 12
}
