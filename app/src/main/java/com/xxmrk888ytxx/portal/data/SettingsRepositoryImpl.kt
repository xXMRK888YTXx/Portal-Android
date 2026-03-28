package com.xxmrk888ytxx.portal.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.portal.domain.model.PortalSettings
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : SettingsRepository {

    private val biometricAuthEnabled = booleanPreferencesKey("biometricAuthEnabled")
    private val additionalPasswordAuthEnabled =
        booleanPreferencesKey("additionalPasswordAuthEnabled")
    private val removePairedClientsIfBiometricEnvironmentChanged =
        booleanPreferencesKey("removePairedClientsIfBiometricEnvironmentChanged")
    private val pairedClientsWasRemovedBySecurityChanges =
        booleanPreferencesKey("pairedClientsWasRemovedBySecurityChanges")


    override val portalSettings: Flow<PortalSettings> = combine(
        preferencesStorage.getProperty(biometricAuthEnabled, false),
        preferencesStorage.getProperty(additionalPasswordAuthEnabled, false),
        preferencesStorage.getProperty(removePairedClientsIfBiometricEnvironmentChanged, false),
        preferencesStorage.getProperty(pairedClientsWasRemovedBySecurityChanges, false)
    ) { flowArray ->
        val biometricAuthEnabled = flowArray[0]
        val additionalPasswordAuthEnabled = flowArray[1]
        val removePairedClientsIfBiometricEnvironmentChanged = flowArray[2]
        val pairedClientsWasRemovedBySecurityChanges = flowArray[3]
        PortalSettings(
            isBiometricAuthEnabled = biometricAuthEnabled,
            isAdditionalPasswordAuthEnabled = additionalPasswordAuthEnabled,
            isRemovePairedClientsIfBiometricEnvironmentChangedEnabled = removePairedClientsIfBiometricEnvironmentChanged,
            isPairedClientsWasRemoveBySecurityChanges = pairedClientsWasRemovedBySecurityChanges
        )
    }

    override suspend fun updateBiometricAuthEnabled(isEnabled: Boolean) =
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(biometricAuthEnabled, isEnabled)
        }

    override suspend fun updateAdditionalPasswordAuthEnabled(isEnabled: Boolean) =
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(additionalPasswordAuthEnabled, isEnabled)
        }

    override suspend fun updateRemovePairedClientsIfBiometricEnvironmentChanged(isEnabled: Boolean) =
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(removePairedClientsIfBiometricEnvironmentChanged,isEnabled)
        }

    override suspend fun updatePairedClientsWasRemovedBySecurityChanges(isWasRemoved: Boolean) =
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(pairedClientsWasRemovedBySecurityChanges,isWasRemoved)
        }
}