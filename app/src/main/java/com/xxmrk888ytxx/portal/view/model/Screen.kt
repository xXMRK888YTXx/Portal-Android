package com.xxmrk888ytxx.portal.view.model

import androidx.navigation3.runtime.NavKey
import com.xxmrk888ytxx.portal.view.mainActivity.model.PortalBottomBarItem
import kotlinx.serialization.Serializable


sealed interface Screen : NavKey {
    @Serializable
    data object OnboardingScreen : Screen

    @Serializable
    data object MainScreenWithBottomBar : Screen, ScreenWithBottomBar {
        override val bottomBarItemId: Int
            get() = PortalBottomBarItem.Devices.id
    }

    @Serializable
    data object AddNewDeviceScreen : Screen

    @Serializable
    data class DeviceConfigurationScreen(val deviceId: String) : Screen
}