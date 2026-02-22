package com.xxmrk888ytxx.corecompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.xxmrk888ytxx.coreandroid.mvi.DefaultSideEffect
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.coreandroid.uiText.asString
import kotlinx.coroutines.flow.Flow

@Composable
inline fun <reified EFFECT : SideEffect> HandleSideEffect(
    sideEffects: Flow<SideEffect>,
    crossinline onEffect: suspend (EFFECT) -> Unit
) {
    val toastManager = LocalToastManager.current
    val context = LocalContext.current
    val navigator = LocalNavigator.current

    LaunchedEffect(sideEffects) {
        sideEffects.collect {
            when(it) {
                is DefaultSideEffect.ShowToast -> toastManager.showToast(it.message.asString(context))
                is DefaultSideEffect.NavigationBack -> navigator.navigateUp()
                is EFFECT -> onEffect(it)
                else -> {}
            }
        }
    }
}