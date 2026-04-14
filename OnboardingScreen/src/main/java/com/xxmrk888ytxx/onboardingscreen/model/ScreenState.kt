package com.xxmrk888ytxx.onboardingscreen.model

data class ScreenState(
    val isTosAccepted: Boolean = false,
    val isNotificationGranted: Boolean = false,
    val isNearbyDevicesGranted: Boolean = false,
    val isOverlayGranted: Boolean = false,
    val isIgnoreBatteryOptimizationsGranted: Boolean = false
)
