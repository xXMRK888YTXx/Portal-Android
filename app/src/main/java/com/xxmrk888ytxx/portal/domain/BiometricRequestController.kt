package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BiometricAuthResult
import kotlinx.coroutines.TimeoutCancellationException

interface BiometricRequestController {
    @Throws(TimeoutCancellationException::class)
    suspend fun waitBiometricAuthResult(timeout: Long = 60000L): BiometricAuthResult
}