package com.xxmrk888ytxx.unlockservice.wifiService

import com.xxmrk888ytxx.unlockservice.core.UnlockMessage
import com.xxmrk888ytxx.unlockservice.core.UnlockRequest
import kotlinx.coroutines.channels.Channel

interface NetworkDriver {
    suspend fun connect(
        host: String,
        messagesForSendChannel: Channel<UnlockMessage>,
        onNewRequestReceived: suspend (UnlockRequest) -> Unit
    )
}