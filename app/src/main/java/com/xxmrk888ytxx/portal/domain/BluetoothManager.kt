package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.connection.BluetoothConnection
import com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice
import kotlinx.coroutines.flow.Flow

typealias MacAddress = String

interface BluetoothManager {
    suspend fun getPairedDevices(): List<PairedBluetoothDevice>
    suspend fun openConnection(macAddress: String): BluetoothConnection
    val pairedDeviceMacAddresses: Flow<Set<MacAddress>?>
    suspend fun updatePairedDeviceMacAddresses()
}