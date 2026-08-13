package com.xxmrk888ytxx.portal.domain

data class WearPermissionState(
    val canPostNotifications: Boolean,
    val canDrawOverlays: Boolean
) {
    val hasAnyPermission: Boolean = canPostNotifications || canDrawOverlays
}

interface WearPermissionChecker {
    fun getState(): WearPermissionState
}
