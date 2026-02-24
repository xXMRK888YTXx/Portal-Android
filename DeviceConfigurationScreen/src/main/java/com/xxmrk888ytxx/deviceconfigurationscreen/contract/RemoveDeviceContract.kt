package com.xxmrk888ytxx.deviceconfigurationscreen.contract

interface RemoveDeviceContract {
    suspend fun removeDevice(deviceId: String): Result<Unit>
}