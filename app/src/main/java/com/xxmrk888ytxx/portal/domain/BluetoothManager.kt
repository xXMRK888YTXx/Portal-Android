package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BluetoothConnection
import com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice

interface BluetoothManager {
    suspend fun getPairedDevices(): List<PairedBluetoothDevice>
    suspend fun openConnection(device: PairedBluetoothDevice): BluetoothConnection
}