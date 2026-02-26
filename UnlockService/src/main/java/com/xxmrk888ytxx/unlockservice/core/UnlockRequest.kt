package com.xxmrk888ytxx.unlockservice.core

sealed interface UnlockRequest {
    data object Auth: UnlockRequest
}