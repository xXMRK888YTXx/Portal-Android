package com.xxmrk888ytxx.portal.providedContract.settingsScreen

import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ChangeMacAddressContract
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import javax.inject.Inject

class ChangeMacAddressContractImpl @Inject constructor(
    private val wifiDeviceRepository: WifiDeviceRepository
) : ChangeMacAddressContract {
    override suspend fun updateWakeOnLanMacAddress(
        deviceId: String,
        macAddress: String
    ) {
        wifiDeviceRepository.updateWOLMacAddress(deviceId,macAddress)
    }
}