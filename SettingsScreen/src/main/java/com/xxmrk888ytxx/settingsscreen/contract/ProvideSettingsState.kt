package com.xxmrk888ytxx.settingsscreen.contract

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow

interface ProvideSettingsState {
    val appVersion: Flow<String>
    val isBiometricProtectionEnabled: Flow<Boolean>
    val isAdditionalPasswordAuthEnabled: Flow<Boolean>
    val isRemovePairedClientsIfBiometricEnvironmentChangedEnabled: Flow<Boolean>
    val isUnsafeUnlockTypesDisabled: Flow<Boolean>
    val isWatchDogEnabled: Flow<Boolean>
    val themeColor: Flow<Color?>
}