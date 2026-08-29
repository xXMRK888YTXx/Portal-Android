package com.xxmrk888ytxx.portal.presentation.mainActivity

/**
 * One-off effects produced by [MainActivityViewModel] and executed by [MainActivity].
 */
sealed interface NavigationSideEffect {
    data class ShowMessage(val message: String) : NavigationSideEffect
    data object OpenNotificationSettings : NavigationSideEffect
}
