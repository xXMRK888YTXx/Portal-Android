package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.connection.BluetoothConnection
import com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice

interface BluetoothManager {
    suspend fun getPairedDevices(): List<PairedBluetoothDevice>
    suspend fun openConnection(macAddress: String): BluetoothConnection
}