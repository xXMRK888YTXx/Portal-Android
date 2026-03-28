package com.xxmrk888ytxx.settingsscreen.contract

interface ChangeSettingsContract {
    suspend fun updateBiometricProtectionState(isEnabled: Boolean)
    suspend fun updateAdditionalPasswordAuthState(isEnabled: Boolean)
    suspend fun updateRemovePairedClientsIfBiometricEnvironmentChangedState(isEnabled: Boolean)
}