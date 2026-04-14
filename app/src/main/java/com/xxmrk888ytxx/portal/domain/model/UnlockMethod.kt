package com.xxmrk888ytxx.portal.domain.model


sealed interface UnlockMethod {
    data object Automatic : UnlockMethod

    data object Notification : UnlockMethod

    data object ConfirmationScreen : UnlockMethod
}