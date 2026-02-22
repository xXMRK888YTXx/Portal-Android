package com.xxmrk888ytxx.deviceconfigurationscreen

import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationUiEvent
import com.xxmrk888ytxx.deviceconfigurationscreen.model.ScreenState
import javax.inject.Inject

class DeviceConfigurationViewModel @Inject constructor() :
    SideEffectPortalViewModel<ScreenState, DeviceConfigurationUiEvent>(ScreenState()) {

    override fun handleEvent(event: DeviceConfigurationUiEvent) {
        TODO("Not yet implemented")
    }
}