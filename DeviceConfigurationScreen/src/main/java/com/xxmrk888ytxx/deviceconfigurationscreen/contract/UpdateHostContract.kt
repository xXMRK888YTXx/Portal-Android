package com.xxmrk888ytxx.deviceconfigurationscreen.contract

interface UpdateHostContract {
    suspend fun update(newHost: String, deviceId: String)
}