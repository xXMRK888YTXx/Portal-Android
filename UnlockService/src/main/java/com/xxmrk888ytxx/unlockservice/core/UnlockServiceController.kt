package com.xxmrk888ytxx.unlockservice.core

import kotlinx.coroutines.flow.Flow

interface UnlockServiceController {
    val unlockRequests: Flow<UnlockRequest>
    fun sendMessage(message: UnlockMessage)
}