package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.deviceconfigurationscreen.exception.DeviceNotFoundException
import com.xxmrk888ytxx.deviceconfigurationscreen.model.Device
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceType
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ProvideDeviceInfoContractImpl @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val certificateRepository: CertificateManager
) : ProvideDeviceInfoContract {
    override suspend fun provideDeviceInfo(deviceId: String): Result<Device> = runCatching(
        Dispatchers.IO
    ) {
        val domainDevice = deviceRepository.getDeviceById(deviceId) ?: throw DeviceNotFoundException(deviceId)
        return@runCatching Device(
            deviceId = domainDevice.deviceId,
            deviceName = domainDevice.deviceName,
            deviceType = DeviceType.WIFI, // TODO Remove hardcoded value
            host = domainDevice.host,
            clientCertificateFingerprint = certificateRepository.getX509CertificateFingerprint(domainDevice.clientCertificate.x509Certificate),
            serverCertificateFingerprint = domainDevice.serverCertificateFingerprint
        )
    }.onFailure { fastDebugLog(it) }
}