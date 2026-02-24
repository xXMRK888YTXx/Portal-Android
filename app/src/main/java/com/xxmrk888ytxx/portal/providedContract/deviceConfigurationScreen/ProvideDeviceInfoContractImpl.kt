package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.deviceconfigurationscreen.exception.DeviceNotFoundException
import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceType
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import io.ktor.util.Hash.combine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ProvideDeviceInfoContractImpl @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val certificateRepository: CertificateManager,
    private val deviceSettingsRepository: DeviceSettingsRepository
) : ProvideDeviceInfoContract {
    override suspend fun provideDeviceInfo(deviceId: String): Flow<Device> {
        val domainDevice = deviceRepository.getDeviceById(deviceId)
        val deviceSetting = deviceSettingsRepository.getDeviceSettingsByDeviceIdOrDefaultSettings(deviceId)
        return combine(domainDevice,deviceSetting) { device, deviceSettings ->
            val device = device ?: throw DeviceNotFoundException(deviceId)
            Device(
                deviceId = device.deviceId,
                deviceName = device.deviceName,
                deviceType = DeviceType.WIFI, // TODO Remove hardcoded value
                host = device.host,
                clientCertificateFingerprint = certificateRepository.getX509CertificateFingerprint(device.clientCertificate.x509Certificate),
                serverCertificateFingerprint = device.serverCertificateFingerprint,
                awaitUnlockRequests = deviceSettings.awaitUnlockRequests
            )
        }
    }
}