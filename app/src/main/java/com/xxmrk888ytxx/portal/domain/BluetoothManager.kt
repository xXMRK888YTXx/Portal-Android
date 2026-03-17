package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BluetoothConnection
import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice

interface BluetoothManager {
    suspend fun getPairedDevices(): List<BluetoothDevice>
    suspend fun openConnection(device: BluetoothDevice): BluetoothConnection
}