package com.xxmrk888ytxx.portal.domain.model

import androidx.compose.ui.graphics.Color
import com.xxmrk888ytxx.settingsscreen.model.AppIcon

data class PortalSettings(
    val isBiometricAuthEnabled: Boolean,
    val isAdditionalPasswordAuthEnabled: Boolean,
    val isRemovePairedClientsIfBiometricEnvironmentChangedEnabled: Boolean,
    val pairedClientsWasRemoveBySecurityChangesCode: Int,
    val isUnsafeUnlockTypesDisabled: Boolean,
    val isOnboardingPassed: Boolean,
    val themeColor: Color?,
    val isWatchDogEnabled: Boolean,
    val appIcon: AppIcon = AppIcon.DEFAULT,
    val unlockScreenStyle: String = "classic"
)
