package com.xxmrk888ytxx.portal.view.model

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


sealed interface Screen : NavKey {
    @Serializable
    data object OnboardingScreen : Screen
    @Serializable
    data object MainScreen : Screen

    @Serializable
    data object AddNewDeviceScreen : Screen
}