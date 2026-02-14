package com.xxmrk888ytxx.coreandroid

import androidx.lifecycle.ViewModel
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.coreandroid.mvi.SideEffectSender
import com.xxmrk888ytxx.coreandroid.mvi.UiEvent
import com.xxmrk888ytxx.coreandroid.mvi.UiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class PortalViewModel<STATE, EVENT : UiEvent> : ViewModel(), UiModel<STATE, EVENT>

abstract class SideEffectPortalViewModel<STATE, EVENT : UiEvent, EFFECT : SideEffect> :
    PortalViewModel<STATE, EVENT>(), SideEffectSender<EFFECT> {
    protected open val sideEffect = MutableSharedFlow<EFFECT>(extraBufferCapacity = 1)
    override val effect: Flow<EFFECT> = sideEffect.asSharedFlow()
}