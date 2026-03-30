package com.xxmrk888ytxx.mainscreen.contract

interface SaveWOLMacAddress {
    suspend fun save(deviceId: String, macAddress: String)
}