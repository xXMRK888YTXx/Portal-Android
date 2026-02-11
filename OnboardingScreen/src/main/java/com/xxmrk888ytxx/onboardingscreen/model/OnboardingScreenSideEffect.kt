package com.xxmrk888ytxx.onboardingscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.SideEffect

sealed interface OnboardingScreenSideEffect : SideEffect {
    object FinishOnboarding : OnboardingScreenSideEffect
}