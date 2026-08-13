package com.xxmrk888ytxx.portal.domain

data class WearPermissionState(
    val canPostNotifications: Boolean
) {
    val canEnterApp: Boolean = canPostNotifications
}

interface WearPermissionChecker {
    fun getState(): WearPermissionState
}
