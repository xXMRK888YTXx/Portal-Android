package com.xxmrk888ytxx.portal.domain

interface PermissionManager {
    val isNotificationPermissionGranted: Boolean
    val isNearbyDevicesPermissionGranted: Boolean
    val isShowSystemAlertPermissionGranted: Boolean
    val isIgnoreBatteryOptimizationsPermissionGranted: Boolean
    fun requestShowSystemAlertPermission()
    fun requestIgnoreBatteryOptimizations()
}