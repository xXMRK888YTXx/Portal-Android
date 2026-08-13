package com.xxmrk888ytxx.portal.presentation.mainActivity

import com.xxmrk888ytxx.portal.domain.WearPermissionState
import com.xxmrk888ytxx.portal.domain.model.Device

data class MainScreenState(
    val selectedDevice: Device? = null,
    val permissions: WearPermissionState = WearPermissionState(
        canPostNotifications = false
    ),
    val screen: WearScreen = WearScreen.Main,
)

enum class WearScreen {
    Main,
    DeviceActions,
    Settings,
    IncomingRequest
}
