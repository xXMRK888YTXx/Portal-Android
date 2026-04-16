package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.mainscreen.contract.SendWOLContract
import com.xxmrk888ytxx.mainscreen.exception.BiometricAuthFailedException
import com.xxmrk888ytxx.mainscreen.model.Device
import com.xxmrk888ytxx.portal.domain.BiometricRequestController
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.portal.domain.WOLServiceManager
import com.xxmrk888ytxx.portal.domain.model.BiometricAuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SendWOLContractImpl @Inject constructor(
    private val wolServiceManager: WOLServiceManager,
    private val biometricRequestController: BiometricRequestController,
    private val settingsRepository: SettingsRepository
) : SendWOLContract {
    override suspend fun sendRequest(
        device: Device,
        isTryToSendUnlockRequestEnabled: Boolean
    ): Result<Unit> = runCatching(Dispatchers.Default) {
        val biometricAuthResult = try {
            if (!settingsRepository.portalSettings.first().isBiometricAuthEnabled)
                BiometricAuthResult.Success
            else
                biometricRequestController.waitBiometricAuthResult(
                    dialogDescription = device.deviceName
                )
        } catch (_: TimeoutCancellationException) {
            BiometricAuthResult.Failed
        }
        if (biometricAuthResult != BiometricAuthResult.Success) throw BiometricAuthFailedException()
        wolServiceManager.startWOLUnlock(device.clientId, isTryToSendUnlockRequestEnabled)
    }
}