package com.xxmrk888ytxx.portal.unlockService

import com.xxmrk888ytxx.portal.data.KtorFactory
import com.xxmrk888ytxx.unlockservice.core.UnlockMessage
import com.xxmrk888ytxx.unlockservice.core.UnlockRequest
import com.xxmrk888ytxx.unlockservice.wifiService.NetworkDriver
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

class WifiDriver @Inject constructor(
    private val ktorFactory: KtorFactory
) : NetworkDriver {

    override suspend fun connect(
        clientId: String,
        messagesForSendChannel: Channel<UnlockMessage>,
        onNewRequestReceived: suspend (UnlockRequest) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}