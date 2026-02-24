package com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen

import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToWifiDeviceContract
import com.xxmrk888ytxx.addnewdevicescreen.model.DeviceSettings as AddNewDeviceScreenDeviceSettings
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.PortalApi
import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ConnectToWifiDeviceContractImpl @Inject constructor(
    private val certificateManager: CertificateManager,
    private val deviceRepository: DeviceRepository,
    private val portalApi: PortalApi,
    private val deviceSettingsRepository: DeviceSettingsRepository
) : ConnectToWifiDeviceContract {

    override suspend fun connectAndProvideSettings(
        deviceName: String,
        host: String,
        pairCode: String
    ): Result<Flow<AddNewDeviceScreenDeviceSettings>> = runCatching(Dispatchers.IO) {
        val clientCertificate = certificateManager.createNewCertificate()
        val pairResult = portalApi.pair(host, pairCode, clientCertificate).getOrThrow()
        deviceRepository.saveDevice(
            Device(
                deviceId = pairResult.clientId,
                deviceName = deviceName,
                host = host,
                clientCertificate = clientCertificate,
                serverCertificateFingerprint = pairResult.certificateFingerprint
            )
        )
        deviceSettingsRepository.getDeviceSettingsByDeviceId(pairResult.clientId).map {
            requireNotNull(it) { "Device settings cannot be null" }
            AddNewDeviceScreenDeviceSettings(pairResult.clientId, it.awaitUnlockRequests)
        }
    }
}