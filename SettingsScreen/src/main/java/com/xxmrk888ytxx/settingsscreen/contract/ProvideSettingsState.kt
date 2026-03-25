package com.xxmrk888ytxx.settingsscreen.contract

import kotlinx.coroutines.flow.Flow

interface ProvideSettingsState {
    val appVersion: Flow<String>
    val isBiometricProtectionEnabled: Flow<Boolean>
}