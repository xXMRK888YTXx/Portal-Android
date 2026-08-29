package com.xxmrk888ytxx.settingsscreen.contract

import androidx.compose.ui.graphics.Color

interface ChangeSettingsContract {
    suspend fun updateBiometricProtectionState(isEnabled: Boolean)
    suspend fun updateAdditionalPasswordAuthState(isEnabled: Boolean)
    suspend fun updateRemovePairedClientsIfBiometricEnvironmentChangedState(isEnabled: Boolean)
    suspend fun updateUnsafeUnlockTypesState(newState: Boolean)
    suspend fun updateWatchDogState(isEnabled: Boolean)
    suspend fun updateThemeColor(newColor: Color?)
}