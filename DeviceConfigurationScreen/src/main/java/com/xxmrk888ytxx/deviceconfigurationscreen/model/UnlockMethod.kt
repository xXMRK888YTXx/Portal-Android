package com.xxmrk888ytxx.deviceconfigurationscreen.model

sealed class UnlockMethod(val isUnsafe: Boolean = false) {

    data class Automatic(val unlockOnlyWhenScreenUnlocked: Boolean) : UnlockMethod(isUnsafe = true)

    data object Notification : UnlockMethod()

    data object ConfirmationScreen : UnlockMethod()

    companion object {
        internal val entries
            get() = listOf(
                ConfirmationScreen,
                Notification,
                Automatic(false),
            )
    }
}