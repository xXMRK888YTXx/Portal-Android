package com.xxmrk888ytxx.unlockservice.core

sealed class UnlockMessage(open val requestId: String?) {
    data class ApproveUnlock(override val requestId: String?): UnlockMessage(requestId)
    data class Canceled(override val requestId: String?): UnlockMessage(requestId)
}