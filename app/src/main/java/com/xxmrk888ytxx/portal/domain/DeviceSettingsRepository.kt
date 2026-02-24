package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.DeviceSettings
import kotlinx.coroutines.flow.Flow

interface DeviceSettingsRepository {
    suspend fun getDeviceSettingsByDeviceId(deviceId: String): Flow<DeviceSettings?>
    suspend fun updateDeviceSettings(deviceSettings: DeviceSettings)
}