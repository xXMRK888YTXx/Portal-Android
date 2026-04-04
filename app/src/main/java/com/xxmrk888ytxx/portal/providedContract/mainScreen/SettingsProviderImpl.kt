package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.SettingsProvider
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsProviderImpl @Inject constructor(
    settingsRepository: SettingsRepository
) : SettingsProvider {
    override val isBiometricProtectionAvailable: Flow<Boolean> = settingsRepository.portalSettings.map { it.isBiometricAuthEnabled }
}