package com.xxmrk888ytxx.unlockservice.core

import kotlinx.coroutines.flow.Flow

interface UnlockServiceController {
    fun getUnlockRequestsForHost(clientId: String): Flow<UnlockRequest>?
    fun sendMessage(clientId: String, message: UnlockMessage)
    fun startListeningUnlockRequest(clientId: String): Flow<UnlockRequest>
    fun stopListening(clientId: String)
}