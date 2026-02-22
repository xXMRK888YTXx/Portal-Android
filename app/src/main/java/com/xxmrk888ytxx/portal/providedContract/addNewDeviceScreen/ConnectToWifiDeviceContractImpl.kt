package com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen

import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToWifiDeviceContract
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.PortalApi
import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ConnectToWifiDeviceContractImpl @Inject constructor(
    private val certificateManager: CertificateManager,
    private val deviceRepository: DeviceRepository,
    private val portalApi: PortalApi
) : ConnectToWifiDeviceContract {

    override suspend fun connect(
        deviceName: String,
        host: String,
        pairCode: String
    ): Result<Unit> = runCatching(Dispatchers.IO) {
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
    }
}