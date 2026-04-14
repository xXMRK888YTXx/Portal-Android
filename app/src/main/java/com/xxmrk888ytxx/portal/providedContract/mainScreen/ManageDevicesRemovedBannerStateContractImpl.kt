package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.ManageDevicesRemovedBannerStateContract
import com.xxmrk888ytxx.mainscreen.model.DevicesRemovedBannerState
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.portal.domain.SettingsRepository.Companion.REMOVED_BY_CHANGES_IN_BIOMETRIC_ENVIRONMENT
import com.xxmrk888ytxx.portal.domain.SettingsRepository.Companion.REMOVED_BY_SECURITY_SETTINGS_CHANGES
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ManageDevicesRemovedBannerStateContractImpl @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ManageDevicesRemovedBannerStateContract {
    override val devicesRemovedBannerState: Flow<DevicesRemovedBannerState> = settingsRepository.portalSettings.map {
        when(it.pairedClientsWasRemoveBySecurityChangesCode) {
            REMOVED_BY_SECURITY_SETTINGS_CHANGES -> DevicesRemovedBannerState.RemovedBySecurityChanges
            REMOVED_BY_CHANGES_IN_BIOMETRIC_ENVIRONMENT -> DevicesRemovedBannerState.RemovedByChangesInBiometricEnvironment
            else -> DevicesRemovedBannerState.None
        }
    }

    override suspend fun resetState() {
        settingsRepository.updatePairedClientsWasRemoveBySecurityChangesCode(SettingsRepository.DEFAULT_VALUE)
    }
}