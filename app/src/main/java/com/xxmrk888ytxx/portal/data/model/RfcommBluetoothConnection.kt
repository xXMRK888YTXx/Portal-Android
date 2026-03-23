package com.xxmrk888ytxx.portal.data.model

import android.bluetooth.BluetoothSocket
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.saveCall
import com.xxmrk888ytxx.portal.domain.model.BluetoothConnection
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RfcommBluetoothConnection(
    private val socket: BluetoothSocket
) : BluetoothConnection {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _isClosed = MutableStateFlow(false)
    override val isClosed: StateFlow<Boolean> = _isClosed.asStateFlow()

    private val _incomingData = MutableSharedFlow<ByteArray>(
        extraBufferCapacity = 64
    )
    override val incomingData = _incomingData.asSharedFlow()

    private val sendChannel = Channel<SendRequest>(Channel.BUFFERED)

    private class SendRequest(
        val data: ByteArray,
        val deferred: CompletableDeferred<Unit> = CompletableDeferred()
    )

    override suspend fun sendData(data: ByteArray) {
        val request = SendRequest(data)
        sendChannel.send(request)
        request.deferred.join()
    }

    override fun trySendData(data: ByteArray) {
        val request = SendRequest(data)
        sendChannel.trySend(request)
    }

    override fun close() {
        if (_isClosed.value) return
        _isClosed.value = true
        saveCall { socket.close() }
        scope.cancel()
        sendChannel.close()
        fastDebugLog("BluetoothConnection closed")
    }

    private fun startSendingData() = scope.launch {
        try {
            val outputStream = socket.outputStream

            for (sendRequest in sendChannel) {
                if (!isActive || _isClosed.value) break
                try {
                    val lengthBytes = ByteBuffer.allocate(4)
                        .order(ByteOrder.BIG_ENDIAN)
                        .putInt(sendRequest.data.size)
                        .array()


                    outputStream.write(lengthBytes)
                    outputStream.write(sendRequest.data)
                    outputStream.flush()
                    sendRequest.deferred.complete(Unit)
                    fastDebugLog("Bluetooth sent data: ${sendRequest.data.size} bytes")
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

    private fun startListeningData() = scope.launch {
        try {
            val inputStream = socket.inputStream

            while (isActive && !_isClosed.value) {
                val lengthBytes = readExact(inputStream, 4)
                val length = ByteBuffer.wrap(lengthBytes).order(ByteOrder.BIG_ENDIAN).int

                if (length !in 1..65536) {
                    fastDebugLog("Protocol desynchronization. Invalid length: $length")
                    throw IOException("Protocol desynchronization. Invalid length: $length")
                }

                val payload = readExact(inputStream, length)
                fastDebugLog("Bluetooth received data: ${payload.size} bytes")
                _incomingData.emit(payload)
            }
        } catch (e: IOException) {
            fastDebugLog("Bluetooth read error or socket closed: ${e.message}")
        } finally {
            withContext(NonCancellable) { close() }
        }
    }

    private fun readExact(input: InputStream, count: Int): ByteArray {
        val buf = ByteArray(count)
        var offset = 0
        while (offset < count) {
            val read = input.read(buf, offset, count - offset)
            if (read == -1) throw IOException("Connection closed")
            offset += read
        }
        return buf
    }

    init {
        if (!socket.isConnected) {
            close()
            error("Socket is not connected")
        }
        startListeningData()
        startSendingData()
    }
}