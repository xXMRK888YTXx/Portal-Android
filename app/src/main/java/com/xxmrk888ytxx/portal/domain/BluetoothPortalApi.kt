package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.BluetoothPairResult

interface BluetoothPortalApi {
    suspend fun pair(pairedBluetoothDevice: PairedBluetoothDevice, pairCode: String): BluetoothPairResult
    suspend fun unlock(bluetoothDevice: BluetoothDevice): Boolean
}