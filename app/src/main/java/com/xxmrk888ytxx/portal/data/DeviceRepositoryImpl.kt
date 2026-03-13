package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.database.dao.DeviceDao
import com.xxmrk888ytxx.database.entry.DeviceEntry
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.SecureStorage
import com.xxmrk888ytxx.portal.domain.ShortcutManager
import com.xxmrk888ytxx.portal.domain.ShortcutRepository
import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao,
    private val secureStorage: SecureStorage,
    private val shortcutManager: ShortcutManager,
    private val shortcutRepository: ShortcutRepository
) : DeviceRepository {
    override suspend fun saveDevice(device: Device) = withContext(Dispatchers.IO) {
        val keyAlias = UUID.randomUUID().toString()
        secureStorage.saveCertificateByAlias(keyAlias, device.clientCertificate)
        deviceDao.upsertDevice(
            DeviceEntry(
                deviceId = device.deviceId,
                deviceName = device.deviceName,
                host = device.host,
                serverCertificateFingerprint = device.serverCertificateFingerprint,
                clientCertificateKeyAlias = keyAlias
            )
        )
    }

    override fun getDeviceById(deviceId: String): Flow<Device?> =
        deviceDao.getDeviceById(deviceId).map { deviceEntry ->
            deviceEntry?.toDomainModel()
        }

    override suspend fun removeDevice(deviceId: String) = withContext<Unit>(Dispatchers.IO) {
        val deviceShortcuts = shortcutRepository.getShortcutsByDeviceId(deviceId)
        deviceDao.removeDevice(deviceId)
        deviceShortcuts.forEach { shortcut ->
            shortcutManager.removeShortcut(shortcut.shortcutId)
        }
    }

    override suspend fun updateHost(deviceId: String, newHost: String) =
        withContext(Dispatchers.IO) {
            deviceDao.updateHost(deviceId, newHost)
        }

    override val devices: Flow<List<Device>> = deviceDao.devices.map { deviceList ->
        deviceList.map { deviceEntry ->
            deviceEntry.toDomainModel()
        }
    }

    private suspend fun DeviceEntry.toDomainModel(): Device {
        val clientCertificate = secureStorage.restoreCertificateByAlias(clientCertificateKeyAlias)
        return Device(
            deviceId = deviceId,
            deviceName = deviceName,
            host = host,
            clientCertificate = clientCertificate,
            serverCertificateFingerprint = serverCertificateFingerprint
        )
    }

}