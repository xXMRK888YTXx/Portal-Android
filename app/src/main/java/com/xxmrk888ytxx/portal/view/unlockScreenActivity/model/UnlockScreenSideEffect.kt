package com.xxmrk888ytxx.portal.view.unlockScreenActivity.model

import com.xxmrk888ytxx.coreandroid.mvi.SideEffect

interface UnlockScreenSideEffect : SideEffect {
    object Dismiss : UnlockScreenSideEffect
}