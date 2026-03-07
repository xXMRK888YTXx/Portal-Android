package com.xxmrk888ytxx.biometricauthentication.model

sealed interface BiometricState {
    object Available : BiometricState
    object NotEnrolled : BiometricState
    object NotAvailable : BiometricState
    object NotHardware : BiometricState
    object SecurityUpdateRequired : BiometricState
    object Unknown : BiometricState

}
