package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.PortalSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val portalSettings: Flow<PortalSettings>
    suspend fun updateBiometricAuthEnabled(isEnabled: Boolean)
    suspend fun updateAdditionalPasswordAuthEnabled(isEnabled: Boolean)
    suspend fun updateRemovePairedClientsIfBiometricEnvironmentChanged(isEnabled: Boolean)
    suspend fun updatePairedClientsWasRemovedBySecurityChanges(isWasRemoved: Boolean)
}