package com.xxmrk888ytxx.mainscreen.contract

import com.xxmrk888ytxx.mainscreen.model.Permission

interface PermissionContract {
    suspend fun getDeniedPermissions(): List<Permission>
    suspend fun requestShowFullScreenIntentPermission()
    suspend fun requestIgnoreBatteryOptimization()
}