package com.xxmrk888ytxx.portal.domain

import kotlinx.coroutines.flow.Flow

interface BiometricAuthStateProvider {
    val isBiometricAuthAvailable: Flow<Boolean>
    fun updateState()
}