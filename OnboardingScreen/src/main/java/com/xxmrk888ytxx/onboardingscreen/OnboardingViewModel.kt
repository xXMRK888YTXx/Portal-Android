package com.xxmrk888ytxx.onboardingscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.PortalViewModel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.mvi.SideEffectSender
import com.xxmrk888ytxx.coreandroid.mvi.UiModel
import com.xxmrk888ytxx.onboardingscreen.contract.OnboardingFinishedContract
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenSideEffect
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenUiEvent
import com.xxmrk888ytxx.onboardingscreen.model.ScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val onboardingFinishedContract: OnboardingFinishedContract
) : PortalViewModel<ScreenState, OnboardingScreenUiEvent>(), SideEffectSender<OnboardingScreenSideEffect> {

    private val _state = MutableStateFlow(ScreenState())
    private val _effect = MutableSharedFlow<OnboardingScreenSideEffect>(extraBufferCapacity = 1)

    override val state: StateFlow<ScreenState> = _state.asStateFlow()
    override val effect: Flow<OnboardingScreenSideEffect> = _effect.asSharedFlow()

    override fun handleEvent(event: OnboardingScreenUiEvent) {
        when(event) {
            OnboardingScreenUiEvent.NextPage -> onboardingFinished()
        }
    }

    private fun onboardingFinished() {
        viewModelScope.launch {
            onboardingFinishedContract.onBoardingFinished()
            _effect.tryEmit(OnboardingScreenSideEffect.FinishOnboarding)
        }
    }
}