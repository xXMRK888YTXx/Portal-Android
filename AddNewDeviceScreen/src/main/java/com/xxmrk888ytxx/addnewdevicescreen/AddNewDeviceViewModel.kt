package com.xxmrk888ytxx.addnewdevicescreen

import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenSideEffect
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenUiEvent
import com.xxmrk888ytxx.addnewdevicescreen.model.ScreenState
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import javax.inject.Inject

class AddNewDeviceViewModel @Inject constructor() : SideEffectPortalViewModel<ScreenState, AddNewDeviceScreenUiEvent, AddNewDeviceScreenSideEffect>(ScreenState()) {

    override fun handleEvent(event: AddNewDeviceScreenUiEvent) {
        TODO("Not yet implemented")
    }
}