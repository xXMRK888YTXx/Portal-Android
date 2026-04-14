package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.UnlockServiceMessage

interface UnlockMessageSender {
    suspend fun sendMessage(clientId: String, message: UnlockServiceMessage): Result<Unit>
}