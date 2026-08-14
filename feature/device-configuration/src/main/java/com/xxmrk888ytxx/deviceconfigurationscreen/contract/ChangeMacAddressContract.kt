package com.xxmrk888ytxx.deviceconfigurationscreen.contract

interface ChangeMacAddressContract {
    suspend fun updateWakeOnLanMacAddress(clientId: String, macAddress: String)
}