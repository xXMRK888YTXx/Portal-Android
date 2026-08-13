package com.xxmrk888ytxx.portal.presentation.mainActivity

sealed interface NavigationSideEffect {
    data class ShowMessage(val message: String) : NavigationSideEffect
    data object OpenNotificationSettings : NavigationSideEffect
}
