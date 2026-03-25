package com.xxmrk888ytxx.portal.providedContract.settingsScreen

import com.xxmrk888ytxx.portal.BuildConfig
import com.xxmrk888ytxx.settingsscreen.contract.ProvideSettingsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ProvideSettingsStateImpl @Inject constructor() : ProvideSettingsState {
    override val appVersion: Flow<String> = flowOf(
        "${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE} (${BuildConfig.VERSION_CODE})"
    )
    override val isBiometricProtectionEnabled: Flow<Boolean> = flowOf(true)
}