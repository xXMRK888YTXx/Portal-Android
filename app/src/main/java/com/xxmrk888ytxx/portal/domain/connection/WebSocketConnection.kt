package com.xxmrk888ytxx.portal.domain.connection

import com.xxmrk888ytxx.portal.data.model.WebSocketEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.Closeable

interface WebSocketConnection : Closeable {
    val isConnected: StateFlow<Boolean>
    val isClosed: StateFlow<Boolean>
    val eventFlow: Flow<WebSocketEvent>
    suspend fun send(data: String)
}