package com.xxmrk888ytxx.unlockservice.core

sealed interface UnlockRequest {
    data class Auth(val clientId: String): UnlockRequest
}