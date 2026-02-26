package com.xxmrk888ytxx.unlockservice.core

sealed interface UnlockMessage {
    data object Unlock: UnlockMessage
}