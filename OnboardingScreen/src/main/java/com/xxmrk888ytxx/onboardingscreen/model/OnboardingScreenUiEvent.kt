package com.xxmrk888ytxx.onboardingscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface OnboardingScreenUiEvent : UiEvent {
    object NextPage : OnboardingScreenUiEvent
}