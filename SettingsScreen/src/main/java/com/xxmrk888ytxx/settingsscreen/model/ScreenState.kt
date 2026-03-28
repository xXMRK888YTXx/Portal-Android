package com.xxmrk888ytxx.settingsscreen.model

data class ScreenState(
    val isBiometricProtectionEnabled: Boolean = false,
    val appVersion: String = "",
    val isAdditionalPasswordAuthEnabled: Boolean = false,
    val isRemovePairedClientsIfBiometricEnvironmentChangedEnabled: Boolean = false
)
