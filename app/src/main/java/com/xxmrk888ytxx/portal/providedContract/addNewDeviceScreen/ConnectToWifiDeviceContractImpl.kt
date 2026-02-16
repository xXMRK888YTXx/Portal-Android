package com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen

import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToWifiDeviceContract
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ConnectToWifiDeviceContractImpl @Inject constructor(
    private val certificateManager: CertificateManager,
    private val deviceRepository: DeviceRepository,
) : ConnectToWifiDeviceContract {

    override suspend fun connect(
        host: String,
        pairCode: String
    ): Result<Unit> = runCatching(Dispatchers.IO) {
        TODO()
    }
}