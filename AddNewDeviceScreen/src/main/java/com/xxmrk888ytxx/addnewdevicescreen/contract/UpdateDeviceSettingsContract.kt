package com.xxmrk888ytxx.addnewdevicescreen.contract

interface UpdateDeviceSettingsContract {
    suspend fun updateAwaitUnlockRequests(deviceId: String, value: Boolean)
}