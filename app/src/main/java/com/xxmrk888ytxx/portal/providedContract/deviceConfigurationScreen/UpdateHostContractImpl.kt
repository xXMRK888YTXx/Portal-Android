package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.coreandroid.saveCall
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.UpdateHostContract
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import javax.inject.Inject

class UpdateHostContractImpl @Inject constructor(
    private val deviceRepository: DeviceRepository
) : UpdateHostContract {
    override suspend fun update(newHost: String, deviceId: String) = saveCall {
        deviceRepository.updateHost(deviceId, newHost)
    }
}