package com.xxmrk888ytxx.portal.providedContract.settingsScreen

import androidx.compose.ui.graphics.Color
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.settingsscreen.contract.ChangeSettingsContract
import javax.inject.Inject

class ChangeSettingsContractImpl @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ChangeSettingsContract {
    override suspend fun updateBiometricProtectionState(isEnabled: Boolean) {
        settingsRepository.updateBiometricAuthEnabled(isEnabled)
    }

    override suspend fun updateAdditionalPasswordAuthState(isEnabled: Boolean) {
        settingsRepository.updateAdditionalPasswordAuthEnabled(isEnabled)
    }

    override suspend fun updateRemovePairedClientsIfBiometricEnvironmentChangedState(isEnabled: Boolean) {
        settingsRepository.updateRemovePairedClientsIfBiometricEnvironmentChanged(isEnabled)
    }

    override suspend fun updateUnsafeUnlockTypesState(newState: Boolean) {
        settingsRepository.updateUnsafeUnlockTypesDisabled(newState)
    }

    override suspend fun updateThemeColor(newColor: Color?) {
        settingsRepository.updateThemeColor(newColor)
    }
}