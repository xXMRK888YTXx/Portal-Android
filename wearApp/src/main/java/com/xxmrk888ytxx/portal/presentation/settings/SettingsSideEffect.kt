package com.xxmrk888ytxx.portal.presentation.settings

/**
 * One-off effects emitted by [SettingsViewModel] for host-level actions.
 */
sealed interface SettingsSideEffect {
    data object NavigateBack : SettingsSideEffect
    data object OpenNotificationSettings : SettingsSideEffect
}
