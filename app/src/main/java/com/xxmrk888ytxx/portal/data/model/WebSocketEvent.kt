package com.xxmrk888ytxx.portal.data.model

import okhttp3.Response
import okio.ByteString

sealed class WebSocketEvent {
    data class Opened(val response: Response) : WebSocketEvent()
    data class TextMessage(val text: String) : WebSocketEvent()
    data class BinaryMessage(val bytes: ByteString) : WebSocketEvent()
    data class Closing(val code: Int, val reason: String) : WebSocketEvent()
    data class Closed(val code: Int, val reason: String) : WebSocketEvent()
    data class Failure(val throwable: Throwable, val response: Response?) : WebSocketEvent()
}