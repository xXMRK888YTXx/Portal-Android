package com.xxmrk888ytxx.mainscreen.contract

import kotlinx.coroutines.flow.Flow

interface SettingsProvider {
    val isBiometricProtectionAvailable: Flow<Boolean>
}