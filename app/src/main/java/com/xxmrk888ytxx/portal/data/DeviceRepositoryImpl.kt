package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.database.dao.DeviceDao
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.ShortcutManager
import com.xxmrk888ytxx.portal.domain.ShortcutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao,
    private val shortcutRepository: ShortcutRepository,
    private val shortcutManager: ShortcutManager,
) : DeviceRepository {
    override suspend fun removeDevice(deviceId: String) = withContext<Unit>(Dispatchers.IO) {
        val deviceShortcuts = shortcutRepository.getShortcutsByDeviceId(deviceId)
        deviceDao.removeDevice(deviceId)
        deviceShortcuts.forEach { shortcut ->
            shortcutManager.removeShortcut(shortcut.shortcutId)
        }
    }

    override suspend fun removeAllDevices() = withContext(Dispatchers.IO) {
        deviceDao.devices.first().forEach { removeDevice(it.deviceId) }
    }
}