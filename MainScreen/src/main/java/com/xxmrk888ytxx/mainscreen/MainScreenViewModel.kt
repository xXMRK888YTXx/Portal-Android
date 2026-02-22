package com.xxmrk888ytxx.mainscreen

import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.MainScreenSideEffect
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import javax.inject.Inject

class MainScreenViewModel @Inject constructor() : SideEffectPortalViewModel<ScreenState, MainScreenEvent>(ScreenState()) {

    override fun handleEvent(event: MainScreenEvent) {
        when(event) {
            MainScreenEvent.AddNewDevice -> sideEffect.tryEmit(MainScreenSideEffect.NavigateToAddNewDeviceScreen)
        }
    }
}