package com.xxmrk888ytxx.unlockservice.core

import kotlinx.coroutines.flow.Flow

interface UnlockServiceController {
    fun startListeningUnlockRequest(clientId: String): Flow<UnlockRequest>
    fun stopListeningUnlockRequest(clientId: String)
    fun getUnlockRequestsForHost(clientId: String): Flow<UnlockRequest>?
    fun sendMessage(clientId: String, message: UnlockMessage)
    fun setIdleModCallback(callback: IdleModDetectedCallback)
}