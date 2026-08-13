package com.xxmrk888ytxx.deviceconfigurationscreen.contract

import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod

interface ChangeDeviceSettingsContract {
    suspend fun updateAwaitUnlockRequestsState(clientId: String, newState: Boolean)
    suspend fun updateSearchIpDynamicallyState(clientId: String, newState: Boolean)
    suspend fun updateUnlockMethodState(clientId: String, newMethod: UnlockMethod)
    suspend fun updateUnlockOnlyWhenScreenUnlockedState(clientId: String, newValue: Boolean)
    suspend fun updateForwardUnlockRequestsToWearState(clientId: String, newValue: Boolean)
    suspend fun updateHost(newHost: String, clientId: String)
    suspend fun updateDeviceName(newName: String, clientId: String)
}
