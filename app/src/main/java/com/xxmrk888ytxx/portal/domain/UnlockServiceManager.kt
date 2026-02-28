package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage
import com.xxmrk888ytxx.portal.exception.ServiceControllerException
import com.xxmrk888ytxx.unlockservice.core.UnlockRequest
import kotlinx.coroutines.flow.Flow

interface UnlockServiceManager {
    suspend fun startListeningUnlockRequest(clientId: String): Result<Flow<UnlockRequest>>
    suspend fun stopListeningUnlockRequest(clientId: String): Result<Unit>
    suspend fun sendMessageToHost(clientId: String,message: UnlockServiceMessage): Result<Unit>
}