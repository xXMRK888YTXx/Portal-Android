package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.BiometricEnvironmentEventHandler
import javax.inject.Inject

class BiometricEnvironmentEventHandlerImpl @Inject constructor() : BiometricEnvironmentEventHandler {
    override suspend fun onBiometricEnvironmentChanged() {
        fastDebugLog("onBiometricEnvironmentChanged")
    }
}