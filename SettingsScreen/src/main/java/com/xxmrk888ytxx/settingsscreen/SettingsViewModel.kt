package com.xxmrk888ytxx.settingsscreen

import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.settingsscreen.model.ScreenState
import com.xxmrk888ytxx.settingsscreen.model.SettingsScreenEvent
import javax.inject.Inject

class SettingsViewModel @Inject constructor() : SideEffectPortalViewModel<ScreenState, SettingsScreenEvent>(
    ScreenState()
) {
    override fun handleEvent(event: SettingsScreenEvent) {

    }

}