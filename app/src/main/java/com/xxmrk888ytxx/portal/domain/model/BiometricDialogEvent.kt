package com.xxmrk888ytxx.portal.domain.model

sealed interface BiometricDialogEvent {
    data object Success : BiometricDialogEvent
    data object Failed : BiometricDialogEvent
    data object Error : BiometricDialogEvent
    data object Canceled : BiometricDialogEvent
}
