package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.RemoveDeviceContract
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class RemoveDeviceContractImpl @Inject constructor(
    private val wifiDeviceRepository: WifiDeviceRepository
) : RemoveDeviceContract {
    override suspend fun removeDevice(deviceId: String) = runCatching(Dispatchers.IO) {
        wifiDeviceRepository.removeDevice(deviceId)
    }
}