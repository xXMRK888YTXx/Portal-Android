package com.xxmrk888ytxx.unlockservice.wifiService

import com.xxmrk888ytxx.unlockservice.core.UnlockMessage
import com.xxmrk888ytxx.unlockservice.core.UnlockRequest
import com.xxmrk888ytxx.unlockservice.exception.InvalidClientIdException
import kotlinx.coroutines.channels.Channel

interface NetworkDriver {
    @Throws(InvalidClientIdException::class)
    suspend fun connect(
        clientId: String,
        messagesForSendChannel: Channel<UnlockMessage>,
        receivedRequestChannel: Channel<UnlockRequest>,
    )
}