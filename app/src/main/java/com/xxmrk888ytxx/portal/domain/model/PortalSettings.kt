package com.xxmrk888ytxx.portal.domain.model

data class PortalSettings(
    val isBiometricAuthEnabled: Boolean,
    val isAdditionalPasswordAuthEnabled: Boolean,
    val isRemovePairedClientsIfBiometricEnvironmentChangedEnabled: Boolean,
    val pairedClientsWasRemoveBySecurityChangesCode: Int
)
