package com.xxmrk888ytxx.addnewdevicescreen.contract

interface ConnectToWifiDeviceContract {
    suspend fun connect(deviceName: String, host: String, pairCode: String): Result<Unit>
}