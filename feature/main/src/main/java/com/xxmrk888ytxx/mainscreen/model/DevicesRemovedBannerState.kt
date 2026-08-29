package com.xxmrk888ytxx.mainscreen.model

sealed interface DevicesRemovedBannerState {
    data object None: DevicesRemovedBannerState
    data object RemovedBySecurityChanges: DevicesRemovedBannerState
    data object RemovedByChangesInBiometricEnvironment: DevicesRemovedBannerState
}