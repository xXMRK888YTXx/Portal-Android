package com.xxmrk888ytxx.portal.data.model

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.saveCall
import com.xxmrk888ytxx.portal.di.module.WebSocketConnection
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.io.IOException
import kotlin.text.Charsets.UTF_8

class WebSocketConnectionImpl(
    private val okHttpClient: OkHttpClient,
    private val url: String,
) : WebSocketListener(), WebSocketConnection {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    private val webSocket: WebSocket by lazy {
        val request = Request.Builder().url(url).build()
        okHttpClient.newWebSocket(request,this)
    }

    private class SendRequest(
        val data: String,
        val deferred: CompletableDeferred<Unit> = CompletableDeferred()
    )

    private val _isConnected = MutableStateFlow(false)
    override val isConnected = _isConnected.asStateFlow()

    private val _isClosed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isClosed: StateFlow<Boolean> = _isClosed.asStateFlow()
    private val _eventFlow = MutableSharedFlow<WebSocketEvent>(
        extraBufferCapacity = 64
    )
    override val eventFlow = _eventFlow
        .asSharedFlow()

    override suspend fun send(data: String) {
        val request = SendRequest(data)
        sendChannel.send(request)
        request.deferred.join()
    }

    private val sendChannel = Channel<SendRequest>(Channel.BUFFERED)

    private fun startSendingData() = scope.launch {
        try {
            for (sendRequest in sendChannel) {
                if (!isActive || _isClosed.value) break
                try {
                    webSocket.send(sendRequest.data)
                    sendRequest.deferred.complete(Unit)
                } catch (e: Exception) {
                    sendRequest.deferred.completeExceptionally(e)
                    throw e
                }
            }
        } catch (e: IOException) {
            fastDebugLog("Bluetooth write error: ${e.message}")
        } finally {
            withContext(NonCancellable) { close() }
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        fastDebugLog("WebSocket opened")
        scope.launch {
            _isConnected.emit(true)
            _eventFlow.emit(WebSocketEvent.Opened(response))
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        fastDebugLog("WebSocket message received: $text")
        scope.launch { _eventFlow.emit(WebSocketEvent.TextMessage(text)) }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        fastDebugLog("WebSocket message received: ${bytes.size} bytes")
        scope.launch { _eventFlow.emit(WebSocketEvent.BinaryMessage(bytes)) }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        fastDebugLog("WebSocket closing: $code $reason")
        scope.launch {
            _eventFlow.emit(WebSocketEvent.Closing(code, reason))
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        fastDebugLog("WebSocket closed: $code $reason")
        scope.launch {
            _eventFlow.emit(WebSocketEvent.Closed(code, reason))
        }
        close()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        fastDebugLog("WebSocket failure: ${t.message}")
        scope.launch {
            _eventFlow.emit(WebSocketEvent.Failure(t, response))
        }
        close()
    }

    override fun close() {
        if (_isClosed.value) return
        _isConnected.tryEmit(false)
        _isClosed.tryEmit(true)
        sendChannel.close()
        saveCall { webSocket.close(1000, "Canceled") }
        scope.cancel()
    }

    init {
        startSendingData()
        webSocket
    }

}