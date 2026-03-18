package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.coreandroid.saveCall
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.UpdateHostContract
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import javax.inject.Inject

class UpdateHostContractImpl @Inject constructor(
    private val wifiDeviceRepository: WifiDeviceRepository
) : UpdateHostContract {
    override suspend fun update(newHost: String, deviceId: String) = saveCall {
        wifiDeviceRepository.updateHost(deviceId, newHost)
    }
}