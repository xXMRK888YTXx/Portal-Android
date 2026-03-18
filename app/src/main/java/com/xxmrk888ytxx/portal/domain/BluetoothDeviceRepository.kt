package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import kotlinx.coroutines.flow.Flow

interface BluetoothDeviceRepository {
    val devices: Flow<List<BluetoothDevice>>
    suspend fun saveDevice(bluetoothDevice: BluetoothDevice)
}