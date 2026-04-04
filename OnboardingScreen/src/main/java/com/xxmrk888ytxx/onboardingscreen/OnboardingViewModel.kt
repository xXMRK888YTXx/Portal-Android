package com.xxmrk888ytxx.onboardingscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.onboardingscreen.contract.OnboardingFinishedContract
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenSideEffect
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenUiEvent
import com.xxmrk888ytxx.onboardingscreen.model.ScreenState
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val onboardingFinishedContract: OnboardingFinishedContract
) : SideEffectPortalViewModel<ScreenState, OnboardingScreenUiEvent>(
    ScreenState()
) {

    override fun handleEvent(event: OnboardingScreenUiEvent) {
        when(event) {
            OnboardingScreenUiEvent.NextPage -> onboardingFinished()
            OnboardingScreenUiEvent.FinishOnboarding -> {}
            OnboardingScreenUiEvent.OpenGithub -> {}
            OnboardingScreenUiEvent.OpenSecuritySettings -> {}
            OnboardingScreenUiEvent.RequestNotificationPermission -> {}
            is OnboardingScreenUiEvent.TosAcceptedChanged -> {}
        }
    }

    private fun onboardingFinished() {
        viewModelScope.launch {
            onboardingFinishedContract.onBoardingFinished()
            sideEffect.tryEmit(OnboardingScreenSideEffect.FinishOnboarding)
        }
    }
}