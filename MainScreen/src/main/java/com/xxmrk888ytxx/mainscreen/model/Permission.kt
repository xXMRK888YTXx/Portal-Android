package com.xxmrk888ytxx.mainscreen.model

sealed interface Permission {
    data object Notification: Permission
    data object NearbyDevices: Permission
    data object ShowSystemAlertPermission: Permission
    data object IgnoreBatteryOptimizations: Permission
}