package com.xxmrk888ytxx.portal.providedContract.onboardingScreen

import com.xxmrk888ytxx.onboardingscreen.contract.PermissionContract
import com.xxmrk888ytxx.onboardingscreen.model.PermissionState
import com.xxmrk888ytxx.portal.domain.PermissionManager
import javax.inject.Inject

class PermissionContractImpl @Inject constructor(
    private val permissionManager: PermissionManager
) : PermissionContract{
    override suspend fun providePermissionState(): PermissionState = PermissionState(
        isNotificationGranted = permissionManager.isNotificationPermissionGranted,
        isNearbyDevicesGranted = permissionManager.isNearbyDevicesPermissionGranted,
        isOverlayGranted = permissionManager.isShowSystemAlertPermissionGranted
    )

    override suspend fun requestOverlayPermission() {
        permissionManager.requestShowSystemAlertPermission()
    }
}