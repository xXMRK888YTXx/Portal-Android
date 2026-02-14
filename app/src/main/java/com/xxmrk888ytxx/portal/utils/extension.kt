package com.xxmrk888ytxx.goals.extensions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xxmrk888ytxx.coreandroid.PortalViewModel
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.coreandroid.mvi.SideEffectSender
import com.xxmrk888ytxx.coreandroid.mvi.UiEvent
import com.xxmrk888ytxx.portal.PortalApp
import com.xxmrk888ytxx.portal.di.AppComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Provider


internal val Context.appComponent: AppComponent
    get() = when (this) {
        is PortalApp -> appComponent
        else -> applicationContext.appComponent
    }

@Composable
inline fun <STATE, EVENT : UiEvent, PVM : PortalViewModel<STATE, EVENT>> ScreenContent(
    content: @Composable (state: STATE, onEvent: (EVENT) -> Unit) -> Unit,
    portalViewModelFactory: Provider<PVM>
) {
    val viewModel: PVM = viewModel { portalViewModelFactory.get() }
    val state by viewModel.state.collectAsState()
    content(state, viewModel::handleEvent)
}

@Composable
inline fun <STATE, EVENT : UiEvent, EFFECT : SideEffect, PVM> ScreenContent(
    content: @Composable (state: STATE, onEvent: (EVENT) -> Unit, sideEffect: Flow<EFFECT>) -> Unit,
    portalViewModelFactory: Provider<PVM>
) where PVM : PortalViewModel<STATE, EVENT>, PVM : SideEffectSender<EFFECT> {
    val viewModel: PVM = viewModel { portalViewModelFactory.get() }
    val state by viewModel.state.collectAsState()
    content(state, viewModel::handleEvent, viewModel.effect)
}