package com.xxmrk888ytxx.mainscreen.contract

interface SaveWOLMacAddress {
    suspend fun save(clientId: String, macAddress: String)
}