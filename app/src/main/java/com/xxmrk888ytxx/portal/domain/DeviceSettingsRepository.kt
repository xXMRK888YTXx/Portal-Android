package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.DeviceSettings
import kotlinx.coroutines.flow.Flow

interface DeviceSettingsRepository {
    val deviceSettings: Flow<List<DeviceSettings>>
    suspend fun getDeviceSettingsByDeviceId(deviceId: String): Flow<DeviceSettings?>
    suspend fun updateAwaitUnlockRequests(deviceId: String,newValue: Boolean)
    suspend fun updateSearchIpDynamically(deviceId: String,newValue: Boolean)
}