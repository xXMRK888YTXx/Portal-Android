package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ChangeDeviceSettingsContract
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.model.DeviceSettings
import javax.inject.Inject

class ChangeDeviceSettingsContractImpl @Inject constructor(
    private val deviceSettingsRepository: DeviceSettingsRepository
) : ChangeDeviceSettingsContract {
    override suspend fun updateAwaitUnlockRequestsState(
        deviceId: String,
        newState: Boolean
    ) {
        deviceSettingsRepository.updateDeviceSettings(DeviceSettings(deviceId, newState))
    }

    override suspend fun updateSearchIpDynamicallyState(
        deviceId: String,
        newState: Boolean
    ) {
        TODO("Not yet implemented")
    }
}