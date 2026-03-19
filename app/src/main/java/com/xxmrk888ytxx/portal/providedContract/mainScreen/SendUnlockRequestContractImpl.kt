package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.SendUnlockRequestContract
import com.xxmrk888ytxx.mainscreen.exception.BiometricAuthFailedException
import com.xxmrk888ytxx.mainscreen.model.DeviceType
import com.xxmrk888ytxx.portal.domain.BiometricRequestController
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import com.xxmrk888ytxx.portal.domain.model.BiometricAuthResult
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.xxmrk888ytxx.mainscreen.model.Device as MainScreenDevice

class SendUnlockRequestContractImpl @Inject constructor(
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val deviceUnlockManager: DeviceUnlockManager,
    private val biometricRequestController: BiometricRequestController,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository
) : SendUnlockRequestContract {
    override suspend fun unlock(device: MainScreenDevice): Result<Unit> = runCatching {
        val biometricAuthResult =  try {
            biometricRequestController.waitBiometricAuthResult() == BiometricAuthResult.Success
        }catch (_: TimeoutCancellationException) {
            null
        }
        if (biometricAuthResult != true) throw BiometricAuthFailedException()

        return@runCatching when(device.deviceType) {
            DeviceType.WIFI -> {
                val savedDevice = wifiDeviceRepository.getDeviceById(device.deviceId).first() ?: throw IllegalArgumentException("Device with deviceId = ${device.deviceId} not exist")
                deviceUnlockManager.unlockWifiDevice(savedDevice).getOrThrow()
            }
            DeviceType.BLUETOOTH -> {
               val savedDevice = bluetoothDeviceRepository.getDeviceById(device.deviceId).first() ?: throw IllegalArgumentException("Device with deviceId = ${device.deviceId} not exist")
                deviceUnlockManager.unlockBluetoothDevice(bluetoothDevice = savedDevice).getOrThrow()
            }
        }
    }
}