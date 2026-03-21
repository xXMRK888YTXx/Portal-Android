package com.xxmrk888ytxx.unlockservice.core

sealed class UnlockRequest(open val requestId: String?) {
    data class Auth(override val requestId: String?): UnlockRequest(requestId)
}