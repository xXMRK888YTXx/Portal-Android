package com.xxmrk888ytxx.onboardingscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.onboardingscreen.contract.OnboardingFinishedContract
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenSideEffect
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenUiEvent
import com.xxmrk888ytxx.onboardingscreen.model.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val onboardingFinishedContract: OnboardingFinishedContract
) : SideEffectPortalViewModel<ScreenState, OnboardingScreenUiEvent, OnboardingScreenSideEffect>() {

    private val _state = MutableStateFlow(ScreenState())

    override val state: StateFlow<ScreenState> = _state.asStateFlow()

    override fun handleEvent(event: OnboardingScreenUiEvent) {
        when(event) {
            OnboardingScreenUiEvent.NextPage -> onboardingFinished()
        }
    }

    private fun onboardingFinished() {
        viewModelScope.launch {
            onboardingFinishedContract.onBoardingFinished()
            sideEffect.tryEmit(OnboardingScreenSideEffect.FinishOnboarding)
        }
    }
}