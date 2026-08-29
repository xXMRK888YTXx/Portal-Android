package com.xxmrk888ytxx.portal.providedContract.settingsScreen

import androidx.compose.ui.graphics.Color
import com.xxmrk888ytxx.portal.BuildConfig
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.settingsscreen.contract.ProvideSettingsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProvideSettingsStateImpl @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ProvideSettingsState {
    override val appVersion: Flow<String> = flowOf(
        if (BuildConfig.DEBUG) {
            "${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE} (${BuildConfig.VERSION_CODE})"
        } else {
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        }
    )
    override val isBiometricProtectionEnabled: Flow<Boolean> =
        settingsRepository.portalSettings.map { it.isBiometricAuthEnabled }
    override val isAdditionalPasswordAuthEnabled: Flow<Boolean> =
        settingsRepository.portalSettings.map { it.isAdditionalPasswordAuthEnabled }
    override val isRemovePairedClientsIfBiometricEnvironmentChangedEnabled: Flow<Boolean> =
        settingsRepository.portalSettings.map { it.isRemovePairedClientsIfBiometricEnvironmentChangedEnabled }
    override val isUnsafeUnlockTypesDisabled: Flow<Boolean> =
        settingsRepository.portalSettings.map { it.isUnsafeUnlockTypesDisabled }
    override val isWatchDogEnabled: Flow<Boolean> =
        settingsRepository.portalSettings.map { it.isWatchDogEnabled }
    override val themeColor: Flow<Color?>
        get() = settingsRepository.portalSettings.map { it.themeColor }
}