package com.xxmrk888ytxx.portal.presentation.settings

sealed interface SettingsSideEffect {
    data object NavigateBack : SettingsSideEffect
    data object OpenNotificationSettings : SettingsSideEffect
}
