package com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen

import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToWifiDeviceContract
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.WifiPortalApi
import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ConnectToWifiDeviceContractImpl @Inject constructor(
    private val certificateManager: CertificateManager,
    private val deviceRepository: DeviceRepository,
    private val wifiPortalApi: WifiPortalApi,
) : ConnectToWifiDeviceContract {

    override suspend fun connectAndDeviceId(
        deviceName: String,
        host: String,
        pairCode: String
    ): Result<String> = runCatching(Dispatchers.IO) {
        val clientCertificate = certificateManager.createNewCertificate()
        val pairResult = wifiPortalApi.pair(host, pairCode, clientCertificate).getOrThrow()
        deviceRepository.saveDevice(
            Device(
                deviceId = pairResult.clientId,
                deviceName = deviceName,
                host = host,
                clientCertificate = clientCertificate,
                serverCertificateFingerprint = pairResult.certificateFingerprint
            )
        )
        return@runCatching pairResult.clientId
    }
}