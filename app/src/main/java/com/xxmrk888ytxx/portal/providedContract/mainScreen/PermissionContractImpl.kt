package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.PermissionContract
import com.xxmrk888ytxx.mainscreen.model.Permission
import com.xxmrk888ytxx.portal.domain.PermissionManager
import javax.inject.Inject

class PermissionContractImpl @Inject constructor(
    private val permissionManager: PermissionManager
) : PermissionContract {
    override suspend fun getDeniedPermissions(): List<Permission> {
        val permissionList = mutableListOf<Permission>()

        if (!permissionManager.isNotificationPermissionGranted) {
            permissionList.add(Permission.Notification)
        }

        if (!permissionManager.isNearbyDevicesPermissionGranted) {
            permissionList.add(Permission.NearbyDevices)
        }

        if (!permissionManager.isShowSystemAlertPermissionGranted) {
            permissionList.add(Permission.ShowSystemAlertPermission)
        }

        return permissionList
    }

    override suspend fun requestShowFullScreenIntentPermission() {
        permissionManager.requestShowSystemAlertPermission()
    }

}