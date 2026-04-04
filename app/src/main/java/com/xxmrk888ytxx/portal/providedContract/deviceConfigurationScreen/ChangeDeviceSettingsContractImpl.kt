package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.coreandroid.saveCall
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ChangeDeviceSettingsContract
import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.utils.toDomainModel
import javax.inject.Inject

class ChangeDeviceSettingsContractImpl @Inject constructor(
    private val deviceSettingsRepository: DeviceSettingsRepository,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository
) : ChangeDeviceSettingsContract {
    override suspend fun updateAwaitUnlockRequestsState(
        clientId: String,
        newState: Boolean
    ) {
        deviceSettingsRepository.updateAwaitUnlockRequests(clientId, newState)
    }

    override suspend fun updateSearchIpDynamicallyState(
        clientId: String,
        newState: Boolean
    ) {
        deviceSettingsRepository.updateSearchIpDynamically(clientId, newState)
    }

    override suspend fun updateUnlockMethodState(
        clientId: String,
        newMethod: UnlockMethod
    ) {
        deviceSettingsRepository.updateUnlockMethod(clientId, newMethod.toDomainModel())
    }

    override suspend fun updateUnlockOnlyWhenScreenUnlockedState(
        clientId: String,
        newValue: Boolean
    ) {
        deviceSettingsRepository.updateUnlockOnlyWhenScreenUnlockedState(clientId, newValue)
    }

    override suspend fun updateHost(newHost: String, clientId: String) = saveCall {
        //Only for wifi devices
        wifiDeviceRepository.updateHost(clientId, newHost)
    }

    override suspend fun updateDeviceName(newName: String, clientId: String) {
        wifiDeviceRepository.updateDeviceName(clientId, newName)
        bluetoothDeviceRepository.updateDeviceName(clientId, newName)
    }
}