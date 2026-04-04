package com.xxmrk888ytxx.portal.domain

interface PermissionManager {
    val isNotificationPermissionGranted: Boolean
    val isNearbyDevicesPermissionGranted: Boolean
    val isShowSystemAlertPermissionGranted: Boolean
    fun requestShowSystemAlertPermission()
}