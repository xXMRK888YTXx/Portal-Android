package com.xxmrk888ytxx.deviceconfigurationscreen.contract

import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod

interface ChangeDeviceSettingsContract {
    suspend fun updateAwaitUnlockRequestsState(deviceId: String, newState: Boolean)
    suspend fun updateSearchIpDynamicallyState(deviceId: String, newState: Boolean)
    suspend fun updateUnlockMethodState(deviceId: String, newMethod: UnlockMethod)
    suspend fun updateUnlockOnlyWhenScreenUnlockedState(deviceId: String, newValue: Boolean)
    suspend fun updateHost(newHost: String, deviceId: String)
    suspend fun updateDeviceName(newName: String, deviceId: String)
}