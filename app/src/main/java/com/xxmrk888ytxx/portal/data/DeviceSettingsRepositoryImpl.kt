package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.database.dao.DeviceSettingsDao
import com.xxmrk888ytxx.database.entry.DeviceSettingsEntry
import com.xxmrk888ytxx.database.model.UnlockMethod as DatabaseUnlockMethod
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.portal.domain.model.DeviceSettings
import com.xxmrk888ytxx.portal.domain.model.UnlockMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeviceSettingsRepositoryImpl @Inject constructor(
    private val deviceSettingsDao: DeviceSettingsDao,
    private val settingsRepository: SettingsRepository,
) : DeviceSettingsRepository {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val deviceSettings: Flow<List<DeviceSettings>> =
        deviceSettingsDao.deviceSettings.map { list ->
            list.map { entry -> entry.toDomainModel() }
        }

    override suspend fun getDeviceSettingsByDeviceId(deviceId: String): Flow<DeviceSettings?> =
        deviceSettingsDao.getDeviceSettingsByDeviceId(deviceId)
            .map {
                val entry = it ?: return@map null
                entry.toDomainModel()
            }

    override suspend fun updateAwaitUnlockRequests(
        deviceId: String,
        newValue: Boolean
    ) = withContext(Dispatchers.IO) {
        deviceSettingsDao.updateAwaitUnlockRequests(
            deviceId = deviceId,
            awaitUnlockRequests = newValue
        )
    }

    override suspend fun updateSearchIpDynamically(
        deviceId: String,
        newValue: Boolean
    ) = withContext(Dispatchers.IO) {
        deviceSettingsDao.updateSearchIpDynamically(deviceId, newValue)
    }

    override suspend fun updateUnlockMethod(
        deviceId: String,
        newMethod: UnlockMethod
    ) = withContext(Dispatchers.IO) {
        deviceSettingsDao.updateUnlockMethod(deviceId, newMethod.toDatabaseModel().id)
    }

    override suspend fun updateUnlockOnlyWhenScreenUnlockedState(
        deviceId: String,
        newValue: Boolean
    ) = withContext(Dispatchers.IO) {
        deviceSettingsDao.updateUnlockOnlyWhenScreenUnlockedState(deviceId, newValue)
    }

    override suspend fun getAllDevicesWithNotSecureUnlockMethod(): List<DeviceSettings> = withContext(Dispatchers.IO) {
        deviceSettingsDao.getAllDevicesWithNotSecureUnlockMethod().map { it.toDomainModel() }.also {
            fastDebugLog("getAllDevicesWithNotSecureUnlockMethod $it")
        }
    }

    private fun DeviceSettingsEntry.toDomainModel(): DeviceSettings {
        return DeviceSettings(
            deviceId = deviceId,
            awaitUnlockRequests = awaitUnlockRequests,
            searchIpDynamically = searchIpDynamically,
            unlockMethod = unlockMethod.toDomainModel(),
            unlockOnlyWhenScreenUnlocked = unlockOnlyWhenScreenUnlocked
        )
    }

    private fun DatabaseUnlockMethod.toDomainModel(): UnlockMethod = when (this) {
        DatabaseUnlockMethod.AUTOMATIC -> UnlockMethod.Automatic
        DatabaseUnlockMethod.CONFIRMATION_SCREEN -> UnlockMethod.ConfirmationScreen
        DatabaseUnlockMethod.NOTIFICATION -> UnlockMethod.Notification
    }

    private fun UnlockMethod.toDatabaseModel(): DatabaseUnlockMethod = when (this) {
        is UnlockMethod.Automatic -> DatabaseUnlockMethod.AUTOMATIC
        UnlockMethod.Notification -> DatabaseUnlockMethod.NOTIFICATION
        UnlockMethod.ConfirmationScreen -> DatabaseUnlockMethod.CONFIRMATION_SCREEN
    }

    init {
        scope.launch {
            settingsRepository
                .portalSettings
                .map { it.isUnsafeUnlockTypesDisabled }
                .distinctUntilChanged()
                .collect { isDisabled ->
                    if (isDisabled) {
                        getAllDevicesWithNotSecureUnlockMethod().forEach {
                            updateUnlockMethod(it.deviceId, UnlockMethod.Notification)
                        }
                    }
                }
        }
    }
}