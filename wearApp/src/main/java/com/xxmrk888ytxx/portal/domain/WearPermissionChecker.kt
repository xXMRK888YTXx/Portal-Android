package com.xxmrk888ytxx.portal.domain

/**
 * Runtime permission snapshot used by the Wear OS permission gate.
 *
 * Notification permission is required because inactive watches receive incoming unlock requests
 * through notifications rather than overlays or full-screen intents.
 */
data class WearPermissionState(
    val canPostNotifications: Boolean
) {
    val canEnterApp: Boolean = canPostNotifications
}

/**
 * Reads the current Wear OS permission state.
 */
interface WearPermissionChecker {
    fun getState(): WearPermissionState
}
