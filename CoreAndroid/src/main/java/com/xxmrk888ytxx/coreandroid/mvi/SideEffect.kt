package com.xxmrk888ytxx.coreandroid.mvi

import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.uiText.UiText

interface SideEffect


sealed interface DefaultSideEffect : SideEffect {
    data class ShowToast(val message: UiText) : DefaultSideEffect
    object NavigationBack : DefaultSideEffect
    data class NavigationAction(val action: Navigator.() -> Unit) : DefaultSideEffect
}