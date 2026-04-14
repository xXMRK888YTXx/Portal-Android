package com.xxmrk888ytxx.onboardingscreen.model

data class PermissionState(
    val isNotificationGranted: Boolean = false,
    val isNearbyDevicesGranted: Boolean = false,
    val isOverlayGranted: Boolean = false,
    val isIgnoreBatteryOptimizationsGranted: Boolean = false,
)
