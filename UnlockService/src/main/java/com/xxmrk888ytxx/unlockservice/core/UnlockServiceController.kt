package com.xxmrk888ytxx.unlockservice.core

import kotlinx.coroutines.flow.Flow

interface UnlockServiceController {
    fun getUnlockRequestsForHost(host: String): Flow<UnlockRequest>?
    fun sendMessage(host: String, message: UnlockMessage)
    fun startListeningUnlockRequest(host: String)
    fun stopListening(host: String)
}