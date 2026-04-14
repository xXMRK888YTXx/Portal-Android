package com.xxmrk888ytxx.portal.view.mainActivity.model

import com.xxmrk888ytxx.coreandroid.mvi.SideEffect

interface MainActivitySideEffect : SideEffect {
    data object FinishActivity : MainActivitySideEffect
}