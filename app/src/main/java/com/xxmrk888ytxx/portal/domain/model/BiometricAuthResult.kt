package com.xxmrk888ytxx.portal.domain.model

sealed class BiometricAuthResult {
    object Success : BiometricAuthResult()
    object Failed : BiometricAuthResult()
}
