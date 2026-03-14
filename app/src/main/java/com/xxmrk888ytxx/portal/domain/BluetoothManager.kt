package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice

interface BluetoothManager {
    suspend fun getPairedDevices(): List<BluetoothDevice>
}