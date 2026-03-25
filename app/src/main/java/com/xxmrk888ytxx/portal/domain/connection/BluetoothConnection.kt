package com.xxmrk888ytxx.portal.domain.connection

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.Closeable

interface BluetoothConnection : Closeable {
    val isClosed: StateFlow<Boolean>
    val incomingData: Flow<ByteArray>
    suspend fun sendData(data: ByteArray)
    fun trySendData(data: ByteArray)
    fun acquire()
    fun release()
}