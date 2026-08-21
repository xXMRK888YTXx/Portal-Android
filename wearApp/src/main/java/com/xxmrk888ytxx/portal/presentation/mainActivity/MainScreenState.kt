package com.xxmrk888ytxx.portal.presentation.mainActivity

import com.xxmrk888ytxx.portal.domain.WearPermissionState
import com.xxmrk888ytxx.portal.domain.model.Device

/**
 * State owned by the Wear OS activity shell.
 *
 * Screen-specific data is kept in screen ViewModels; this state only tracks top-level navigation
 * and permission gate status.
 */
data class MainScreenState(
    val selectedDevice: Device? = null,
    val permissions: WearPermissionState = WearPermissionState(
        canPostNotifications = false
    ),
    val screen: WearScreen = WearScreen.Main,
)

/**
 * Top-level destination currently shown by the single activity Wear app.
 */
enum class WearScreen {
    Main,
    DeviceActions,
    Settings
}
