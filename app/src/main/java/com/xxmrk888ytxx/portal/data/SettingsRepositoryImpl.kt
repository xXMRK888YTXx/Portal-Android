package com.xxmrk888ytxx.portal.data

import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.portal.domain.SettingsRepository.Companion.DEFAULT_VALUE
import com.xxmrk888ytxx.portal.domain.SettingsRepository.Companion.REMOVED_BY_SECURITY_SETTINGS_CHANGES
import com.xxmrk888ytxx.portal.domain.model.PortalSettings
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage,
    private val deviceRepository: DeviceRepository
) : SettingsRepository {

    private val biometricAuthEnabled = booleanPreferencesKey("biometricAuthEnabled")
    private val additionalPasswordAuthEnabled =
        booleanPreferencesKey("additionalPasswordAuthEnabled")
    private val removePairedClientsIfBiometricEnvironmentChanged =
        booleanPreferencesKey("removePairedClientsIfBiometricEnvironmentChanged")
    private val pairedClientsWasRemovedBySecurityChanges =
        intPreferencesKey("pairedClientsWasRemovedBySecurityChanges")

    private val isUnsafeUnlockTypesDisabledKey =
        booleanPreferencesKey("isUnsafeUnlockTypesDisabledKey")
    private val isOnboardingPassedKey = booleanPreferencesKey("is_onboarding_passed")
    private val themeColorKey = longPreferencesKey("theme_color")


    override val portalSettings: Flow<PortalSettings> = combine<Any?, PortalSettings>(
        preferencesStorage.getProperty(biometricAuthEnabled, false),
        preferencesStorage.getProperty(additionalPasswordAuthEnabled, false),
        preferencesStorage.getProperty(removePairedClientsIfBiometricEnvironmentChanged, false),
        preferencesStorage.getProperty(pairedClientsWasRemovedBySecurityChanges, DEFAULT_VALUE),
        preferencesStorage.getProperty(isUnsafeUnlockTypesDisabledKey, false),
        preferencesStorage.getProperty(isOnboardingPassedKey, false),
        preferencesStorage.getPropertyOrNull(themeColorKey)
    ) { flowArray ->
        val biometricAuthEnabled = flowArray[0] as Boolean
        val additionalPasswordAuthEnabled = flowArray[1] as Boolean
        val removePairedClientsIfBiometricEnvironmentChanged = flowArray[2] as Boolean
        val pairedClientsWasRemovedBySecurityChanges = flowArray[3] as Int
        val isUnsafeUnlockTypesDisabled = flowArray[4] as Boolean
        val isOnboardingPassed = flowArray[5] as Boolean
        val themeColor = flowArray[6] as Long?

        PortalSettings(
            isBiometricAuthEnabled = biometricAuthEnabled,
            isAdditionalPasswordAuthEnabled = additionalPasswordAuthEnabled,
            isRemovePairedClientsIfBiometricEnvironmentChangedEnabled = removePairedClientsIfBiometricEnvironmentChanged,
            pairedClientsWasRemoveBySecurityChangesCode = pairedClientsWasRemovedBySecurityChanges,
            isUnsafeUnlockTypesDisabled = isUnsafeUnlockTypesDisabled,
            isOnboardingPassed = isOnboardingPassed,
            themeColor = if (themeColor == null) null else Color(value = themeColor.toULong())
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

            if (!isEnabled) {
                deviceRepository.removeAllDevices()
                updatePairedClientsWasRemoveBySecurityChangesCode(
                    REMOVED_BY_SECURITY_SETTINGS_CHANGES
                )
            }
        }

    override suspend fun updatePairedClientsWasRemoveBySecurityChangesCode(newCode: Int) =
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(pairedClientsWasRemovedBySecurityChanges, newCode)
        }

    override suspend fun updateUnsafeUnlockTypesDisabled(newState: Boolean) = withContext(
        Dispatchers.IO
    ) {
        preferencesStorage.writeProperty(isUnsafeUnlockTypesDisabledKey, newState)
        if (!newState) {
            deviceRepository.removeAllDevices()
            updatePairedClientsWasRemoveBySecurityChangesCode(
                REMOVED_BY_SECURITY_SETTINGS_CHANGES
            )
        }
    }

    override suspend fun markOnboardingAsPassed() = withContext(Dispatchers.IO) {
        preferencesStorage.writeProperty(isOnboardingPassedKey, true)
    }

    override suspend fun updateThemeColor(newColor: Color?) = withContext(Dispatchers.IO) {
        fastDebugLog("updateThemeColor $newColor")
        if (newColor == null) {
            preferencesStorage.removeProperty(themeColorKey)
            return@withContext
        }
        preferencesStorage.writeProperty(themeColorKey, newColor.value.toLong())
    }
}