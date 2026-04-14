package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.database.dao.DeviceDao
import com.xxmrk888ytxx.database.dao.WifiDeviceDao
import com.xxmrk888ytxx.database.entry.WifiDeviceEntry
import com.xxmrk888ytxx.portal.domain.SecureStorage
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
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
    private val wifiDeviceDao: WifiDeviceDao
) : WifiDeviceRepository {
    override suspend fun saveDevice(wifiDevice: WifiDevice) = withContext(Dispatchers.IO) {
        val keyAlias = UUID.randomUUID().toString()
        secureStorage.saveCertificateByAlias(keyAlias, wifiDevice.clientCertificate)
        deviceDao.upsertWifiDevice(
            WifiDeviceEntry(
                clientId = wifiDevice.clientId,
                deviceName = wifiDevice.deviceName,
                host = wifiDevice.host,
                serverCertificateFingerprint = wifiDevice.serverCertificateFingerprint,
                clientCertificateKeyAlias = keyAlias,
                wolMacAddress = wifiDevice.wolMacAddress
            )
        )
    }

    override fun getDeviceById(clientId: String): Flow<WifiDevice?> =
        wifiDeviceDao.getWifiDeviceById(clientId).map { deviceEntry ->
            deviceEntry?.toDomainModel()
        }

    override suspend fun updateHost(clientId: String, newHost: String) =
        withContext(Dispatchers.IO) {
            wifiDeviceDao.updateHost(clientId, newHost)
        }

    override suspend fun updateDeviceName(clientId: String, newName: String) =
        withContext(Dispatchers.IO) {
            wifiDeviceDao.updateDeviceName(deviceId = clientId, newDeviceName = newName)
        }

    override suspend fun updateWOLMacAddress(clientId: String, macAddress: String) =
        withContext(Dispatchers.IO) {
            wifiDeviceDao.updateWOLMacAddress(clientId, macAddress)
        }

    override val devices: Flow<List<WifiDevice>> = wifiDeviceDao.devices.map { deviceList ->
        deviceList.map { deviceEntry ->
            deviceEntry.toDomainModel()
        }
    }

    private suspend fun WifiDeviceEntry.toDomainModel(): WifiDevice {
        val clientCertificate = secureStorage.restoreCertificateByAlias(clientCertificateKeyAlias)
        return WifiDevice(
            clientId = clientId,
            deviceName = deviceName,
            host = host,
            clientCertificate = clientCertificate,
            serverCertificateFingerprint = serverCertificateFingerprint,
            wolMacAddress = wolMacAddress
        )
    }

}