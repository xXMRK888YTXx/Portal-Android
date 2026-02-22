package com.xxmrk888ytxx.mainscreen

import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.mainscreen.contract.ProvideSavedDevices
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.MainScreenSideEffect
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val provideSavedDevices: ProvideSavedDevices
) : SideEffectPortalViewModel<ScreenState, MainScreenEvent>(ScreenState()) {

    override val state: StateFlow<ScreenState> = provideSavedDevices.devices
        .map { ScreenState(it) }.stateWhileSubscribed()


    override fun handleEvent(event: MainScreenEvent) {
        when(event) {
            MainScreenEvent.AddNewDevice -> sideEffect.tryEmit(MainScreenSideEffect.NavigateToAddNewDeviceScreen)
        }
    }
}