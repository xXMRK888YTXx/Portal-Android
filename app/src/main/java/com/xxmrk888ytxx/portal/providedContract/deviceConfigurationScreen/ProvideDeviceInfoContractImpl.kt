package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.deviceconfigurationscreen.exception.DeviceNotFoundException
import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceType
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ProvideDeviceInfoContractImpl @Inject constructor(
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val certificateRepository: CertificateManager,
    private val deviceSettingsRepository: DeviceSettingsRepository
) : ProvideDeviceInfoContract {
    override suspend fun provideDeviceInfo(deviceId: String): Flow<Device> {
        val domainDevice = wifiDeviceRepository.getDeviceById(deviceId)
        val deviceSetting = deviceSettingsRepository.getDeviceSettingsByDeviceId(deviceId)
        return combine(domainDevice,deviceSetting) { device, deviceSettings ->
            val device = device ?: throw DeviceNotFoundException(deviceId)
            val deviceSettings = deviceSettings ?: throw DeviceNotFoundException(deviceId)
            Device(
                deviceId = device.deviceId,
                deviceName = device.deviceName,
                deviceType = DeviceType.WIFI, // TODO Remove hardcoded value
                host = device.host,
                clientCertificateFingerprint = certificateRepository.getX509CertificateFingerprint(device.clientCertificate.x509Certificate),
                serverCertificateFingerprint = device.serverCertificateFingerprint,
                awaitUnlockRequests = deviceSettings.awaitUnlockRequests,
                searchIpDynamically = deviceSettings.searchIpDynamically
            )
        }
    }
}