package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.PortalSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val portalSettings: Flow<PortalSettings>
    suspend fun updateBiometricAuthEnabled(isEnabled: Boolean)
    suspend fun updateAdditionalPasswordAuthEnabled(isEnabled: Boolean)
    suspend fun updateRemovePairedClientsIfBiometricEnvironmentChanged(isEnabled: Boolean)
    suspend fun updatePairedClientsWasRemoveBySecurityChangesCode(newCode: Int)
    suspend fun updateUnsafeUnlockTypesDisabled(newState: Boolean)
    suspend fun markOnboardingAsPassed()

    companion object {
        const val DEFAULT_VALUE = 0
        const val REMOVED_BY_SECURITY_SETTINGS_CHANGES = 1
        const val REMOVED_BY_CHANGES_IN_BIOMETRIC_ENVIRONMENT = 2
    }
}