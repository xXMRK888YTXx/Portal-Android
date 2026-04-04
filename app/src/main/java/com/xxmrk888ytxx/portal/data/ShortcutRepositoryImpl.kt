package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.database.dao.ShortcutDao
import com.xxmrk888ytxx.database.entry.ShortcutEntry
import com.xxmrk888ytxx.portal.data.model.Shortcut
import com.xxmrk888ytxx.portal.domain.ShortcutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShortcutRepositoryImpl @Inject constructor(
    private val shortcutDao: ShortcutDao
) : ShortcutRepository {
    override suspend fun registerShortcut(shortcut: Shortcut) = withContext(Dispatchers.IO) {
        shortcutDao.addShortcut(shortcut.toEntry())
    }

    override suspend fun removeShortcut(shortcutId: String) = withContext(Dispatchers.IO) {
        shortcutDao.removeShortcut(shortcutId)
    }

    override suspend fun getShortcutById(shortcutId: String): Shortcut? = withContext(Dispatchers.IO) {
        shortcutDao.getShortcut(shortcutId)?.toDomainModel()
    }

    override suspend fun getShortcutsByDeviceId(deviceId: String): List<Shortcut> = withContext(Dispatchers.IO) {
        shortcutDao.getShortcutsByDeviceId(deviceId).map { it.toDomainModel() }
    }

    override suspend fun getShortcutWithInsecureUnlock(): List<Shortcut> = withContext(Dispatchers.IO) {
        shortcutDao.getShortcutWithInsecureUnlock().map { it.toDomainModel() }
    }

    override suspend fun updateIsRequiredBiometricUnlock(
        shortcutId: String,
        newValue: Boolean
    ) {
        shortcutDao.updateIsRequiredBiometricUnlock(shortcutId, newValue)
    }

    private fun Shortcut.toEntry(): ShortcutEntry = ShortcutEntry(
        shortcutId = shortcutId,
        deviceId = clientId,
        isRequiredBiometricUnlock = isRequiredBiometricUnlock,
        isSendWOLRequest = isSendWOLRequest
    )

    private fun ShortcutEntry.toDomainModel() = Shortcut(
        shortcutId = shortcutId,
        clientId = deviceId,
        isRequiredBiometricUnlock = isRequiredBiometricUnlock,
        isSendWOLRequest = isSendWOLRequest
    )
}