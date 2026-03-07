package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.SendUnlockRequestContract
import com.xxmrk888ytxx.mainscreen.exception.BiometricAuthFailedException
import com.xxmrk888ytxx.mainscreen.model.DeviceType
import com.xxmrk888ytxx.portal.domain.BiometricRequestController
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import com.xxmrk888ytxx.portal.domain.model.BiometricAuthResult
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.xxmrk888ytxx.mainscreen.model.Device as MainScreenDevice

class SendUnlockRequestContractImpl @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val deviceUnlockManager: DeviceUnlockManager,
    private val biometricRequestController: BiometricRequestController
) : SendUnlockRequestContract {
    override suspend fun unlock(device: MainScreenDevice): Result<Unit> = runCatching {
        val savedDevice = deviceRepository.getDeviceById(device.deviceId).first() ?: throw IllegalArgumentException("Device with deviceId = ${device.deviceId} not exist")
        val biometricAuthResult =  try {
            biometricRequestController.waitBiometricAuthResult() == BiometricAuthResult.Success
        }catch (_: TimeoutCancellationException) {
            null
        }
        if (biometricAuthResult != true) throw BiometricAuthFailedException()

        return@runCatching when(device.deviceType) {
            DeviceType.WIFI -> deviceUnlockManager.unlockWifiDevice(savedDevice).getOrThrow()
            DeviceType.BLUETOOTH -> TODO()
        }
    }
}