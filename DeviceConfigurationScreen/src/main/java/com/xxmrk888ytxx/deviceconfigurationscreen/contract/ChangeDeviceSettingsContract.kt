package com.xxmrk888ytxx.deviceconfigurationscreen.contract

interface ChangeDeviceSettingsContract {
    suspend fun updateAwaitUnlockRequestsState(deviceId: String, newState: Boolean)
}