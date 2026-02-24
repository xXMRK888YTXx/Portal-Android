package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.SendUnlockRequestContract
import com.xxmrk888ytxx.mainscreen.model.DeviceType
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.xxmrk888ytxx.mainscreen.model.Device as MainScreenDevice

class SendUnlockRequestContractImpl @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val deviceUnlockManager: DeviceUnlockManager
) : SendUnlockRequestContract {
    override suspend fun unlock(device: MainScreenDevice): Result<Unit> {
        val savedDevice = deviceRepository.getDeviceById(device.deviceId).first() ?: throw IllegalArgumentException("Device with deviceId = ${device.deviceId} not exist")
        return when(device.deviceType) {
            DeviceType.WIFI -> deviceUnlockManager.unlockWifiDevice(savedDevice)
            DeviceType.BLUETOOTH -> TODO()
        }
    }
}