package com.xxmrk888ytxx.portal.data.model

import android.bluetooth.BluetoothSocket
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.saveCall
import com.xxmrk888ytxx.portal.domain.model.BluetoothConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

class RfcommBluetoothConnection(
    private val socket: BluetoothSocket
) : BluetoothConnection {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _isClosed = MutableStateFlow(false)
    override val isClosed: StateFlow<Boolean> = _isClosed.asStateFlow()

    private val _incomingData = Channel<ByteArray>(Channel.BUFFERED)
    override val incomingData = _incomingData.receiveAsFlow()

    private val sendChannel = Channel<ByteArray>(Channel.BUFFERED)

    override suspend fun sendData(data: ByteArray) {
        sendChannel.send(data)
    }

    override fun close() {
        if (_isClosed.value) return
        _isClosed.value = true
        saveCall { socket.close() }
        scope.cancel()
        _incomingData.close()
        fastDebugLog("BluetoothConnection closed")
    }

    private fun startSendingData() = scope.launch {
        try {
            val outputStream = socket.outputStream
            for (data in sendChannel) {
                if (!isActive || _isClosed.value) break

                outputStream.write(data)
                outputStream.flush()
            }
        } catch (e: IOException) {
            fastDebugLog("Bluetooth write error: ${e.message}")
        } finally {
            withContext(NonCancellable) { close() }
        }
    }

    private fun startListeningData() = scope.launch {
        val buffer = ByteArray(1024)
        try {
            val inputStream = socket.inputStream
            while (isActive && !_isClosed.value) {
                val bytesRead = inputStream.read(buffer)

                if (bytesRead == -1) {
                    break
                }

                if (bytesRead > 0) {
                    _incomingData.send(buffer.copyOf(bytesRead))
                }
            }
        } catch (e: IOException) {
            fastDebugLog("Bluetooth read error or socket closed: ${e.message}")
        } finally {
            withContext(NonCancellable) { close() }
        }
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