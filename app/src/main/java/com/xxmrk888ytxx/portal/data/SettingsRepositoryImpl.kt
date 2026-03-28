package com.xxmrk888ytxx.portal.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
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
        intPreferencesKey("pairedClientsWasRemovedBySecurityChanges")


    override val portalSettings: Flow<PortalSettings> = combine(
        preferencesStorage.getProperty(biometricAuthEnabled, false),
        preferencesStorage.getProperty(additionalPasswordAuthEnabled, false),
        preferencesStorage.getProperty(removePairedClientsIfBiometricEnvironmentChanged, false),
        preferencesStorage.getProperty(pairedClientsWasRemovedBySecurityChanges, 0)
    ) { flowArray ->
        val biometricAuthEnabled = flowArray[0] as Boolean
        val additionalPasswordAuthEnabled = flowArray[1] as Boolean
        val removePairedClientsIfBiometricEnvironmentChanged = flowArray[2] as Boolean
        val pairedClientsWasRemovedBySecurityChanges = flowArray[3] as Int
        PortalSettings(
            isBiometricAuthEnabled = biometricAuthEnabled,
            isAdditionalPasswordAuthEnabled = additionalPasswordAuthEnabled,
            isRemovePairedClientsIfBiometricEnvironmentChangedEnabled = removePairedClientsIfBiometricEnvironmentChanged,
            pairedClientsWasRemoveBySecurityChangesCode = pairedClientsWasRemovedBySecurityChanges
        )
    }

    override suspend fun updateBiometricAuthEnabled(isEnabled: Boolean) =
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(biometricAuthEnabled, isEnabled)
            if (!isEnabled) {
                updateAdditionalPasswordAuthEnabled(false)
                updateRemovePairedClientsIfBiometricEnvironmentChanged(false)
            }
        }

    override suspend fun updateAdditionalPasswordAuthEnabled(isEnabled: Boolean) =
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(additionalPasswordAuthEnabled, isEnabled)
        }

    override suspend fun updateRemovePairedClientsIfBiometricEnvironmentChanged(isEnabled: Boolean) =
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(
                removePairedClientsIfBiometricEnvironmentChanged,
                isEnabled
            )
        }

    override suspend fun updatePairedClientsWasRemovedBySecurityChanges(newCode: Int) =
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(pairedClientsWasRemovedBySecurityChanges, newCode)
        }
}