package com.xxmrk888ytxx.portal.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import com.xxmrk888ytxx.portal.domain.PreferenceManager
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

//TODO Move to PortalSettings
class PreferenceManagerImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : PreferenceManager {

    private val isOnboardingPassedKey = booleanPreferencesKey(IS_ONBOARDING_PASSED_KEY_NAME)


    override val isOnboardingPassed: Flow<Boolean> =
        preferencesStorage.getProperty(isOnboardingPassedKey, false)

    override suspend fun markOnboardingAsPassed() = withContext(Dispatchers.IO) {
        preferencesStorage.writeProperty(isOnboardingPassedKey,true)
    }

    companion object {
        const val IS_ONBOARDING_PASSED_KEY_NAME = "is_onboarding_passed"
    }
}