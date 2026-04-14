package com.xxmrk888ytxx.portal.domain

interface BiometricEnvironmentEventHandler {
    suspend fun onBiometricEnvironmentChanged()
}