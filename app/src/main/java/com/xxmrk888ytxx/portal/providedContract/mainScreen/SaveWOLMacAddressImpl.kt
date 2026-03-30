package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.SaveWOLMacAddress
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import javax.inject.Inject

class SaveWOLMacAddressImpl @Inject constructor(
    private val wifiDeviceRepository: WifiDeviceRepository
) : SaveWOLMacAddress {
    override suspend fun save(deviceId: String, macAddress: String) {
        wifiDeviceRepository.updateWOLMacAddress(deviceId, macAddress)
    }
}