package com.xxmrk888ytxx.mainscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface MainScreenEvent : UiEvent {
    data object AddNewDevice : MainScreenEvent
    data class SendUnlockRequest(val device: Device) : MainScreenEvent

}