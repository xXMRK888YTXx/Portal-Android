package com.xxmrk888ytxx.addnewdevicescreen.contract

interface ConnectToWifiDeviceContract {
    suspend fun connect(host: String, pairCode: String): Result<Unit>
}