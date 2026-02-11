package com.xxmrk888ytxx.portal.view.model

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


sealed interface Screen : NavKey {
    @Serializable
    object OnboardingScreen : Screen
    @Serializable
    object MainScreen : Screen
}