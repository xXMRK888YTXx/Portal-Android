package com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen

import com.xxmrk888ytxx.addnewdevicescreen.contract.UpdateDeviceSettingsContract
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.model.DeviceSettings
import javax.inject.Inject

class UpdateDeviceSettingsContractImpl @Inject constructor(
    private val deviceSettingsRepository: DeviceSettingsRepository
) : UpdateDeviceSettingsContract {
    override suspend fun updateAwaitUnlockRequests(deviceId: String, value: Boolean) {
        deviceSettingsRepository.updateDeviceSettings(DeviceSettings(deviceId, value))
    }
}