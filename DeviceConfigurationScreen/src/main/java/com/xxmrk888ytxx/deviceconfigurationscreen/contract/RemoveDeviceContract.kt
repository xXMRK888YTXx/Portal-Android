package com.xxmrk888ytxx.deviceconfigurationscreen.contract

interface RemoveDeviceContract {
    suspend fun removeDevice(clientId: String): Result<Unit>
}