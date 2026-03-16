package com.xxmrk888ytxx.addnewdevicescreen.contract

import com.xxmrk888ytxx.addnewdevicescreen.model.BluetoothDevice

interface ConnectToBluetoothDeviceContract {
    suspend fun connect(
        deviceName: String,
        pairCode: String,
        bluetoothDevice: BluetoothDevice
    ): Result<String>
}