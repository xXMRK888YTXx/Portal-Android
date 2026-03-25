package com.xxmrk888ytxx.settingsscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface SettingsScreenEvent : UiEvent {
    data object OnLogsClick : SettingsScreenEvent
}