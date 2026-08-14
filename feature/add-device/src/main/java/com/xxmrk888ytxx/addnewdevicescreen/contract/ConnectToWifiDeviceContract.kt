package com.xxmrk888ytxx.addnewdevicescreen.contract

interface ConnectToWifiDeviceContract {
    suspend fun connectAndGetClientId(deviceName: String, host: String, pairCode: String): Result<String>
}