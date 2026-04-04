package com.xxmrk888ytxx.onboardingscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface OnboardingScreenUiEvent : UiEvent {
    object NextPage : OnboardingScreenUiEvent
    data class TosAcceptedChanged(val isAccepted: Boolean) : OnboardingScreenUiEvent
    object RequestNotificationPermission : OnboardingScreenUiEvent
    object OpenSecuritySettings : OnboardingScreenUiEvent
    object OpenGithub : OnboardingScreenUiEvent
    object FinishOnboarding : OnboardingScreenUiEvent
}