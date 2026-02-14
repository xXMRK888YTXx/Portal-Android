package com.xxmrk888ytxx.mainscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface MainScreenEvent : UiEvent {
    object AddNewDevice : MainScreenEvent
}