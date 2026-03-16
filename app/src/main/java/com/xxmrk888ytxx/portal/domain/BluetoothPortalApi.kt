package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.BluetoothPairResult

interface BluetoothPortalApi {
    suspend fun pair(bluetoothDevice: BluetoothDevice, pairCode: String): Result<BluetoothPairResult>
}