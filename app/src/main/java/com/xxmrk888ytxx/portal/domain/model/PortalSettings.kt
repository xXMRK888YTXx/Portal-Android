package com.xxmrk888ytxx.portal.domain.model

import androidx.compose.ui.graphics.Color

data class PortalSettings(
    val isBiometricAuthEnabled: Boolean,
    val isAdditionalPasswordAuthEnabled: Boolean,
    val isRemovePairedClientsIfBiometricEnvironmentChangedEnabled: Boolean,
    val pairedClientsWasRemoveBySecurityChangesCode: Int,
    val isUnsafeUnlockTypesDisabled: Boolean,
    val isOnboardingPassed: Boolean,
    val themeColor: Color?,
    val isWatchDogEnabled: Boolean,
)
