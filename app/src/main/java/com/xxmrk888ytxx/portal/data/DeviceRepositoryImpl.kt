package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.database.dao.DeviceDao
import com.xxmrk888ytxx.database.entry.DeviceEntry
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.SecureStorage
import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao,
    private val secureStorage: SecureStorage,
) : DeviceRepository {
    override suspend fun saveDevice(device: Device) = withContext(Dispatchers.IO) {
        val keyAlias = UUID.randomUUID().toString()
        secureStorage.saveCertificateByAlias(keyAlias, device.clientCertificate)
        deviceDao.insertDevice(
            DeviceEntry(
                deviceId = device.deviceId,
                deviceName = device.deviceName,
                host = device.host,
                serverCertificateFingerprint = device.serverCertificateFingerprint,
                clientCertificateKeyAlias = keyAlias
            )
        )
    }

    override val devices: Flow<List<Device>> = deviceDao.devices.map { deviceList ->
        deviceList.map { deviceEntry ->
            val clientCertificate = secureStorage.restoreCertificateByAlias(deviceEntry.clientCertificateKeyAlias)
            Device(
                deviceId = deviceEntry.deviceId,
                deviceName = deviceEntry.deviceName,
                host = deviceEntry.host,
                clientCertificate = clientCertificate,
                serverCertificateFingerprint = deviceEntry.serverCertificateFingerprint
            )
        }
    }

}