package com.xxmrk888ytxx.deviceconfigurationscreen.model

sealed interface UnlockMethod {

    data class Automatic(val unlockOnlyWhenScreenUnlocked: Boolean) : UnlockMethod

    data object Notification : UnlockMethod

    data object ConfirmationScreen : UnlockMethod

    companion object {
        internal val entries = listOf(
            ConfirmationScreen,
            Notification,
            Automatic(false),
        )
    }
}