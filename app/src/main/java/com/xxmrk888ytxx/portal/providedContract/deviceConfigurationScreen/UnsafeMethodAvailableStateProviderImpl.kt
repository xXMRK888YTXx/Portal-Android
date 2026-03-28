package com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen

import com.xxmrk888ytxx.deviceconfigurationscreen.contract.UnsafeMethodAvailableStateProvider
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UnsafeMethodAvailableStateProviderImpl @Inject constructor(
    private val settingsRepository: SettingsRepository
) : UnsafeMethodAvailableStateProvider {
    override val isDisabled: Flow<Boolean> = settingsRepository.portalSettings.map { it.isUnsafeUnlockTypesDisabled }
}