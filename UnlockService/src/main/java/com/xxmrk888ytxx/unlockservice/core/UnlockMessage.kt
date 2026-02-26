package com.xxmrk888ytxx.unlockservice.core

sealed interface UnlockMessage {
    data class Unlock(val clientId: String): UnlockMessage
}