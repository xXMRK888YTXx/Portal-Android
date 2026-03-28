package com.xxmrk888ytxx.settingsscreen.model

data class ScreenState(
    val bottomSheetState: BottomSheetState = BottomSheetState.None,
    val isBiometricProtectionEnabled: Boolean = false,
    val appVersion: String = "",
    val isAdditionalPasswordAuthEnabled: Boolean = false,
    val isRemovePairedClientsIfBiometricEnvironmentChangedEnabled: Boolean = false
)
