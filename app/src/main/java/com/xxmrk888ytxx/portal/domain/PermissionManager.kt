package com.xxmrk888ytxx.portal.domain

interface PermissionManager {
    val isNotificationPermissionGranted: Boolean
    val isNearbyDevicesPermissionGranted: Boolean
    val isShowFullIntentPermissionGranted: Boolean
    fun requestShowFullScreenIntent()
}