package com.xxmrk888ytxx.mainscreen

import com.xxmrk888ytxx.coreandroid.PortalViewModel
import com.xxmrk888ytxx.coreandroid.mvi.UiModel
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MainScreenViewModel @Inject constructor() : PortalViewModel<ScreenState, MainScreenEvent>() {
    private val _state = MutableStateFlow(ScreenState(Unit))
    override val state: StateFlow<ScreenState> = _state.asStateFlow()

    override fun handleEvent(event: MainScreenEvent) {
        TODO("Not yet implemented")
    }
}