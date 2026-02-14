package com.xxmrk888ytxx.addnewdevicescreen

import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenSideEffect
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenUiEvent
import com.xxmrk888ytxx.addnewdevicescreen.model.ScreenState
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class AddNewDeviceViewModel @Inject constructor() : SideEffectPortalViewModel<ScreenState, AddNewDeviceScreenUiEvent, AddNewDeviceScreenSideEffect>() {

    private val _state = MutableStateFlow(ScreenState())

    override val state: StateFlow<ScreenState> = _state.asStateFlow()

    override fun handleEvent(event: AddNewDeviceScreenUiEvent) {
        TODO("Not yet implemented")
    }
}