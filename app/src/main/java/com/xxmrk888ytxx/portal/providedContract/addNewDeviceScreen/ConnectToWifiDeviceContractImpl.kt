package com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen

import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToWifiDeviceContract
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.WifiPortalApi
import com.xxmrk888ytxx.portal.domain.model.WifiDevice
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ConnectToWifiDeviceContractImpl @Inject constructor(
    private val certificateManager: CertificateManager,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val wifiPortalApi: WifiPortalApi,
) : ConnectToWifiDeviceContract {

    override suspend fun connectAndDeviceId(
        deviceName: String,
        host: String,
        pairCode: String
    ): Result<String> = runCatching(Dispatchers.IO) {
        val clientCertificate = certificateManager.createNewCertificate()
        val pairResult = wifiPortalApi.pair(host, pairCode, clientCertificate).getOrThrow()
        wifiDeviceRepository.saveDevice(
            WifiDevice(
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