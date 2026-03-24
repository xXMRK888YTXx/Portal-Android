package com.xxmrk888ytxx.portal.data.connection

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.saveCall
import com.xxmrk888ytxx.portal.data.model.WebSocketEvent
import com.xxmrk888ytxx.portal.domain.connection.WebSocketConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketConnectionImpl(
    okHttpClient: OkHttpClient,
    private val url: String,
) : WebSocketListener(), WebSocketConnection {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val webSocket: WebSocket = okHttpClient.newWebSocket(
        Request.Builder().url(url).build(),
        this
    )

    private val _isConnected = MutableStateFlow(false)
    override val isConnected = _isConnected.asStateFlow()

    private val _isClosed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isClosed: StateFlow<Boolean> = _isClosed.asStateFlow()
    private val _eventFlow = Channel<WebSocketEvent>(
        Channel.BUFFERED
    )
    override val eventFlow = _eventFlow
        .receiveAsFlow()

    override suspend fun send(data: String) {
        if (_isClosed.value) {
            fastDebugLog("Websocket write error: socket is closed")
            return
        }

        try {
            val enqueued = webSocket.send(data)
            if (!enqueued) {
                fastDebugLog("Websocket write error: queue is full or socket closing")
            }
        } catch (e: Exception) {
            fastDebugLog("Websocket write exception: ${e.message}")
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        fastDebugLog("WebSocket opened")
        _isConnected.value = true
        scope.launch {
            _eventFlow.send(WebSocketEvent.Opened(response))
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        fastDebugLog("WebSocket message received: $text")
        scope.launch { _eventFlow.send(WebSocketEvent.TextMessage(text)) }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        fastDebugLog("WebSocket message received: ${bytes.size} bytes")
        scope.launch { _eventFlow.send(WebSocketEvent.BinaryMessage(bytes)) }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        fastDebugLog("WebSocket closing: $code $reason")
        scope.launch {
            _eventFlow.send(WebSocketEvent.Closing(code, reason))
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        fastDebugLog("WebSocket closed: $code $reason")
        _eventFlow.trySend(WebSocketEvent.Closed(code, reason))
        close()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        fastDebugLog("WebSocket failure: ${t.message}")
        _eventFlow.trySend(WebSocketEvent.Failure(t, response))
        close()
    }

    override fun close() {
        if (_isClosed.value) return
        _isConnected.value = false
        _isClosed.value = true
        _eventFlow.close()
        saveCall { webSocket.close(1000, "Canceled") }
        scope.cancel()
    }
}