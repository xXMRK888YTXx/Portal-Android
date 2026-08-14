package com.xxmrk888ytxx.settingsscreen.contract

import kotlinx.coroutines.flow.Flow

interface BiometricProtectionAvailableStateProvider {
    val isAvailable: Flow<Boolean>
}