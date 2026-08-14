package com.xxmrk888ytxx.settingsscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.SideEffect

sealed interface SettingsScreenSideEffect : SideEffect {
    data object OpenOpenSourceLicenses : SettingsScreenSideEffect
}