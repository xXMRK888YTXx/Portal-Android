package com.xxmrk888ytxx.deviceconfigurationscreen.contract

interface ChangeMacAddressContract {
    suspend fun updateWakeOnLanMacAddress(deviceId: String, macAddress: String)
}