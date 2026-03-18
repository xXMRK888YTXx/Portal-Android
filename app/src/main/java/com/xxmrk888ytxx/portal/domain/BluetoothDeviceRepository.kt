package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.WifiDevice
import kotlinx.coroutines.flow.Flow

interface BluetoothDeviceRepository {
    val devices: Flow<List<BluetoothDevice>>
    suspend fun saveDevice(bluetoothDevice: BluetoothDevice)
    fun getDeviceById(deviceId: String): Flow<BluetoothDevice?>
}