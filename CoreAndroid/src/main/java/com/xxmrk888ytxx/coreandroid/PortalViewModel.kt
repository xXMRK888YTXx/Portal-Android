package com.xxmrk888ytxx.coreandroid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.mvi.DefaultSideEffect
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.coreandroid.mvi.SideEffectSender
import com.xxmrk888ytxx.coreandroid.mvi.UiEvent
import com.xxmrk888ytxx.coreandroid.mvi.UiModel
import com.xxmrk888ytxx.coreandroid.uiText.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

abstract class PortalViewModel<STATE, EVENT : UiEvent>(private val initialState: STATE) : ViewModel(),
    UiModel<STATE, EVENT> {

    protected open val _state = MutableStateFlow(initialState)
    override val state: StateFlow<STATE> = _state.asStateFlow()

    protected fun <T> Flow<T>.stateWhileSubscribed(
        defaultStated: T
    ) = this.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), defaultStated)

    protected fun <T : STATE> Flow<T>.stateWhileSubscribed() = this.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialState)


}


abstract class SideEffectPortalViewModel<STATE, EVENT : UiEvent>(initialState: STATE) :
    PortalViewModel<STATE, EVENT>(initialState), SideEffectSender<SideEffect> {
    protected open val sideEffect = MutableSharedFlow<SideEffect>(extraBufferCapacity = Int.MAX_VALUE)
    override val effect: Flow<SideEffect> = sideEffect.asSharedFlow()

    protected fun sendNavigateUpSideEffect() {
        sideEffect.tryEmit(DefaultSideEffect.NavigationBack)
    }

    protected fun sendToastSideEffect(uiText: UiText) {
        sideEffect.tryEmit(DefaultSideEffect.ShowToast(uiText))
    }

    protected fun sendNavigationAction(action: Navigator.() -> Unit) {
        sideEffect.tryEmit(DefaultSideEffect.NavigationAction(action))
    }
}