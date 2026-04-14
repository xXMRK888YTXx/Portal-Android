package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BiometricAuthRequestOption
import com.xxmrk888ytxx.portal.domain.model.BiometricAuthResult
import kotlinx.coroutines.flow.Flow

interface BiometricActivityResultReceiver {
    val biometricAuthRequestForActivity: Flow<BiometricAuthRequestOption>
    fun onNewBiometricAuthResult(result: BiometricAuthResult)
}