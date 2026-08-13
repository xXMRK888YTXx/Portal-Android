package com.xxmrk888ytxx.portal.presentation.settings

sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
    data object OpenNotificationSettings : SettingsEvent
    data object RefreshPhoneConnection : SettingsEvent
}
