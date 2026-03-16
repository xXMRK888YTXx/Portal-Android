package com.xxmrk888ytxx.portal.domain

interface PermissionManager {
    val isBluetoothPermissionGranted: Boolean
    val isNotificationPermissionGranted: Boolean
    val isNearbyDevicesPermissionGranted: Boolean
    val isShowSystemAlertPermissionGranted: Boolean
    fun requestShowSystemAlertPermission()
}