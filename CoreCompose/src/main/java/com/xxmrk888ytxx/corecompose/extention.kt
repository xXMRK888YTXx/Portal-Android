package com.xxmrk888ytxx.corecompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <EFFECT : SideEffect> HandleSideEffect(sideEffects: Flow<EFFECT>,onEffect: (EFFECT) -> Unit) {
    LaunchedEffect(sideEffects) {
        sideEffects.collect {
            onEffect(it)
        }
    }
}