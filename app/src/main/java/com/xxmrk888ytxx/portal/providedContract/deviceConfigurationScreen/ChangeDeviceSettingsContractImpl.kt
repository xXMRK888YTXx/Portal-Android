package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.coreandroid.saveCall
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ChangeDeviceSettingsContract
import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.model.DeviceSettings
import com.xxmrk888ytxx.portal.utils.toDomainModel
import javax.inject.Inject

class ChangeDeviceSettingsContractImpl @Inject constructor(
    private val deviceSettingsRepository: DeviceSettingsRepository,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository
) : ChangeDeviceSettingsContract {
    override suspend fun updateAwaitUnlockRequestsState(
        deviceId: String,
        newState: Boolean
    ) {
        deviceSettingsRepository.updateAwaitUnlockRequests(deviceId, newState)
    }

    override suspend fun updateSearchIpDynamicallyState(
        deviceId: String,
        newState: Boolean
    ) {
        deviceSettingsRepository.updateSearchIpDynamically(deviceId, newState)
    }

    override suspend fun updateUnlockMethodState(
        deviceId: String,
        newMethod: UnlockMethod
    ) {
        deviceSettingsRepository.updateUnlockMethod(deviceId, newMethod.toDomainModel())
    }

    override suspend fun updateUnlockOnlyWhenScreenUnlockedState(
        deviceId: String,
        newValue: Boolean
    ) {
        deviceSettingsRepository.updateUnlockOnlyWhenScreenUnlockedState(deviceId, newValue)
    }

    override suspend fun updateHost(newHost: String, deviceId: String) = saveCall {
        //Only for wifi devices
        wifiDeviceRepository.updateHost(deviceId, newHost)
    }

    override suspend fun updateDeviceName(newName: String, deviceId: String) {
        wifiDeviceRepository.updateDeviceName(deviceId, newName)
        bluetoothDeviceRepository.updateDeviceName(deviceId, newName)
    }
}