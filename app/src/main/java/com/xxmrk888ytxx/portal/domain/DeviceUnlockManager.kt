package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.WifiDevice

interface DeviceUnlockManager {
    suspend fun unlockWifiDevice(wifiDevice: WifiDevice): Result<Unit>
    suspend fun unlockBluetoothDevice(bluetoothDevice: BluetoothDevice): Result<Unit>
}