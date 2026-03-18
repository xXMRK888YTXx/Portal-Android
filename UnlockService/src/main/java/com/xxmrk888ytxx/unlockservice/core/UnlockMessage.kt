package com.xxmrk888ytxx.unlockservice.core

sealed interface UnlockMessage {
    data object ApproveUnlock: UnlockMessage
    data object Canceled: UnlockMessage
}