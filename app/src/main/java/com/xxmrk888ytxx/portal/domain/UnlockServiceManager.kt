package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest
import kotlinx.coroutines.flow.Flow

interface UnlockServiceManager {
    suspend fun startListeningUnlockRequest(clientId: String): Result<Flow<UnlockServiceRequest>>
    suspend fun stopListeningUnlockRequest(clientId: String): Result<Unit>
    suspend fun sendMessageToHost(clientId: String,message: UnlockServiceMessage): Result<Unit>
}