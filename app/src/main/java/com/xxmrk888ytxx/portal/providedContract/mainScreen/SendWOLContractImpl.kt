package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.SendWOLContract
import com.xxmrk888ytxx.mainscreen.model.Device
import com.xxmrk888ytxx.portal.domain.WOLManager
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SendWOLContractImpl @Inject constructor(
    private val wolManager: WOLManager,
    private val wifiDeviceRepository: WifiDeviceRepository
) : SendWOLContract {
    override suspend fun sendRequest(
        device: Device,
        isTryToSendUnlockRequestEnabled: Boolean
    ): Result<Unit> {
        val macAddress = wifiDeviceRepository.getDeviceById(device.deviceId).first()?.wolMacAddress ?: return Result.failure(
            IllegalStateException("Device without mac address or device not exits"))
        return wolManager.sendWOLRequest(macAddress)
    }
}