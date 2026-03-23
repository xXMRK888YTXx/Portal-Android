package com.xxmrk888ytxx.portal.domain.model

import androidx.datastore.core.Closeable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothConnection : Closeable {
    val isClosed: StateFlow<Boolean>
    val incomingData: Flow<ByteArray>
    suspend fun sendData(data: ByteArray)
    fun trySendData(data: ByteArray)
    fun acquire()
    fun release()
}