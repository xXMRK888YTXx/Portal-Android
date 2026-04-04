package com.xxmrk888ytxx.onboardingscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface OnboardingScreenUiEvent : UiEvent {
    data object NextPage : OnboardingScreenUiEvent
    data class TosAcceptedChanged(val isAccepted: Boolean) : OnboardingScreenUiEvent
    data object RequestNotificationPermission : OnboardingScreenUiEvent
    data object FinishOnboarding : OnboardingScreenUiEvent
    data object OpenAndroidSourceCode : OnboardingScreenUiEvent
    data object OpenPCSourceCode : OnboardingScreenUiEvent
    data object OpenAndroidDevelopGithub : OnboardingScreenUiEvent
    data object OpenPCADeveloperGithub : OnboardingScreenUiEvent
    data object OpenTOSLink : OnboardingScreenUiEvent
    data object OpenPrivacyPolicyLink : OnboardingScreenUiEvent
    data object RequestNearbyDevicesPermission : OnboardingScreenUiEvent
    data object RequestOverlayPermission : OnboardingScreenUiEvent
    data object UpdatePermissionState : OnboardingScreenUiEvent
}