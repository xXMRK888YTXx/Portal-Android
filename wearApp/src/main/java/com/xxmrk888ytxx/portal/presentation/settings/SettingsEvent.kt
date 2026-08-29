package com.xxmrk888ytxx.portal.presentation.settings

/**
 * User intents from the Wear OS settings screen.
 */
sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
    data object OpenNotificationSettings : SettingsEvent
    data object RefreshPhoneConnection : SettingsEvent
}
