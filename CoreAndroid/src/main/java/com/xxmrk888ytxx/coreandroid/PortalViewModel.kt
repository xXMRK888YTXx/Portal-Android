package com.xxmrk888ytxx.coreandroid

import androidx.lifecycle.ViewModel
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.coreandroid.mvi.SideEffectSender
import com.xxmrk888ytxx.coreandroid.mvi.UiEvent
import com.xxmrk888ytxx.coreandroid.mvi.UiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class PortalViewModel<STATE, EVENT : UiEvent>(initialState: STATE) : ViewModel(), UiModel<STATE, EVENT> {

    protected open val _state = MutableStateFlow(initialState)
    override val state: StateFlow<STATE> = _state.asStateFlow()
}


abstract class SideEffectPortalViewModel<STATE, EVENT : UiEvent, EFFECT : SideEffect>(initialState: STATE) :
    PortalViewModel<STATE, EVENT>(initialState), SideEffectSender<EFFECT> {
    protected open val sideEffect = MutableSharedFlow<EFFECT>(extraBufferCapacity = 1)
    override val effect: Flow<EFFECT> = sideEffect.asSharedFlow()
}