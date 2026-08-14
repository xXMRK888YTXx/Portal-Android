package com.xxmrk888ytxx.coreandroid.mvi

import kotlinx.coroutines.flow.Flow

interface SideEffectSender<EFFECT : SideEffect> {
    val effect: Flow<EFFECT>
}