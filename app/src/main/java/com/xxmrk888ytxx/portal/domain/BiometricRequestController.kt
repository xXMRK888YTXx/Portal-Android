package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BiometricAuthResult

interface BiometricRequestController {
    suspend fun waitBiometricAuthResult(timeout: Long = 60000L): BiometricAuthResult
}