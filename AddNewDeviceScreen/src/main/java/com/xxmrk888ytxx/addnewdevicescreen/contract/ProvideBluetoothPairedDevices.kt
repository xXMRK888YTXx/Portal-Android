package com.xxmrk888ytxx.addnewdevicescreen.contract

import com.xxmrk888ytxx.addnewdevicescreen.model.BluetoothDevice

interface ProvideBluetoothPairedDevices {
    suspend fun getPairedDevices(): Result<List<BluetoothDevice>>
}