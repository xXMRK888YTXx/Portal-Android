package com.xxmrk888ytxx.mainscreen

import com.xxmrk888ytxx.coreandroid.PortalViewModel
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.mvi.SideEffectSender
import com.xxmrk888ytxx.coreandroid.mvi.UiModel
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.MainScreenSideEffect
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MainScreenViewModel @Inject constructor() : SideEffectPortalViewModel<ScreenState, MainScreenEvent, MainScreenSideEffect>(), SideEffectSender<MainScreenSideEffect> {
    private val _state = MutableStateFlow(ScreenState(Unit))
    override val state: StateFlow<ScreenState> = _state.asStateFlow()

    override fun handleEvent(event: MainScreenEvent) {
        when(event) {
            MainScreenEvent.AddNewDevice -> sideEffect.tryEmit(MainScreenSideEffect.NavigateToAddNewDeviceScreen)
        }
    }
}