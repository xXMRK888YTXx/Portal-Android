package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.BiometricEnvironmentEventHandler
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BiometricEnvironmentEventHandlerImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val deviceRepository: DeviceRepository
) : BiometricEnvironmentEventHandler {
    override suspend fun onBiometricEnvironmentChanged() {
        fastDebugLog("onBiometricEnvironmentChanged")
        if (!settingsRepository.portalSettings.first().isRemovePairedClientsIfBiometricEnvironmentChangedEnabled) {
            fastDebugLog("isRemovePairedClientsIfBiometricEnvironmentChangedEnabled == false. Skip")
            return
        }
        settingsRepository.updatePairedClientsWasRemoveBySecurityChangesCode(SettingsRepository.REMOVED_BY_CHANGES_IN_BIOMETRIC_ENVIRONMENT)
        deviceRepository.removeAllDevices()
    }
}