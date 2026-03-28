package com.xxmrk888ytxx.portal.view.model

import androidx.fragment.app.FragmentActivity
import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface UnlockScreenUiEvent : UiEvent {
    class Allow(val fragmentActivity: FragmentActivity, val isSentByUser: Boolean) : UnlockScreenUiEvent
    data object Deny : UnlockScreenUiEvent
}