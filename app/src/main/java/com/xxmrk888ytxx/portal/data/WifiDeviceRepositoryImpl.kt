package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.database.dao.DeviceDao
import com.xxmrk888ytxx.database.dao.WifiDeviceDao
import com.xxmrk888ytxx.database.entry.WifiDeviceEntry
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.SecureStorage
import com.xxmrk888ytxx.portal.domain.ShortcutManager
import com.xxmrk888ytxx.portal.domain.ShortcutRepository
import com.xxmrk888ytxx.portal.domain.model.WifiDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class WifiDeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao,
    private val secureStorage: SecureStorage,
    private val shortcutManager: ShortcutManager,
    private val shortcutRepository: ShortcutRepository,
    private val wifiDeviceDao: WifiDeviceDao
) : WifiDeviceRepository {
    override suspend fun saveDevice(wifiDevice: WifiDevice) = withContext(Dispatchers.IO) {
        val keyAlias = UUID.randomUUID().toString()
        secureStorage.saveCertificateByAlias(keyAlias, wifiDevice.clientCertificate)
        deviceDao.upsertWifiDevice(
            WifiDeviceEntry(
                deviceId = wifiDevice.deviceId,
                deviceName = wifiDevice.deviceName,
                host = wifiDevice.host,
                serverCertificateFingerprint = wifiDevice.serverCertificateFingerprint,
                clientCertificateKeyAlias = keyAlias
            )
        )
    }

    override fun getDeviceById(deviceId: String): Flow<WifiDevice?> =
        wifiDeviceDao.getWifiDeviceById(deviceId).map { deviceEntry ->
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
            wifiDeviceDao.updateHost(deviceId, newHost)
        }

    override val devices: Flow<List<WifiDevice>> = wifiDeviceDao.devices.map { deviceList ->
        deviceList.map { deviceEntry ->
            deviceEntry.toDomainModel()
        }
    }

    private suspend fun WifiDeviceEntry.toDomainModel(): WifiDevice {
        val clientCertificate = secureStorage.restoreCertificateByAlias(clientCertificateKeyAlias)
        return WifiDevice(
            deviceId = deviceId,
            deviceName = deviceName,
            host = host,
            clientCertificate = clientCertificate,
            serverCertificateFingerprint = serverCertificateFingerprint
        )
    }

}