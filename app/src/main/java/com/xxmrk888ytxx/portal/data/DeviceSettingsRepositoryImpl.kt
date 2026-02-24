package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.database.dao.DeviceSettingsDao
import com.xxmrk888ytxx.database.entry.DeviceSettingsEntry
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.model.DeviceSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeviceSettingsRepositoryImpl @Inject constructor(
    private val deviceSettingsDao: DeviceSettingsDao
) : DeviceSettingsRepository {

    override suspend fun getDeviceSettingsByDeviceId(deviceId: String): Flow<DeviceSettings?> =
        deviceSettingsDao.getDeviceSettingsByDeviceId(deviceId)
            .map {
                val entry = it ?: return@map null
                DeviceSettings(
                    deviceId = entry.deviceId,
                    awaitUnlockRequests = entry.awaitUnlockRequests
                )
            }

    override suspend fun updateDeviceSettings(deviceSettings: DeviceSettings) =
        withContext(Dispatchers.IO) {
            deviceSettingsDao.upsertDeviceSettings(
                DeviceSettingsEntry(
                    deviceId = deviceSettings.deviceId,
                    awaitUnlockRequests = deviceSettings.awaitUnlockRequests
                )
            )
        }
}