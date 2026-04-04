package com.xxmrk888ytxx.onboardingscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.SideEffect

sealed interface OnboardingScreenSideEffect : SideEffect {
    data object NextPage: OnboardingScreenSideEffect
    data object RequestNotificationPermission: OnboardingScreenSideEffect
    data object RequestNearbyDevicesPermission: OnboardingScreenSideEffect
}