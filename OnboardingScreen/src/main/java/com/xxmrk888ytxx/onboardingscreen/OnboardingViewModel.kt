package com.xxmrk888ytxx.onboardingscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.onboardingscreen.contract.OnboardingFinishedContract
import com.xxmrk888ytxx.onboardingscreen.contract.OpenLinkContract
import com.xxmrk888ytxx.onboardingscreen.contract.PermissionContract
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenSideEffect
import com.xxmrk888ytxx.onboardingscreen.model.OnboardingScreenUiEvent
import com.xxmrk888ytxx.onboardingscreen.model.ScreenState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val onboardingFinishedContract: OnboardingFinishedContract,
    private val openLinkContract: OpenLinkContract,
    private val permissionContract: PermissionContract
) : SideEffectPortalViewModel<ScreenState, OnboardingScreenUiEvent>(
    ScreenState()
) {

    override fun handleEvent(event: OnboardingScreenUiEvent) {
        when (event) {
            OnboardingScreenUiEvent.NextPage -> nextPage()
            OnboardingScreenUiEvent.FinishOnboarding -> onboardingFinished()
            is OnboardingScreenUiEvent.TosAcceptedChanged -> changeTosState(event.isAccepted)
            OnboardingScreenUiEvent.OpenAndroidDevelopGithub -> openLink { openLinkContract.openAndroidDeveloperLink() }
            OnboardingScreenUiEvent.OpenAndroidSourceCode -> openLink { openLinkContract.openAndroidSourceCodeLink() }
            OnboardingScreenUiEvent.OpenPCADeveloperGithub -> openLink { openLinkContract.openPCDeveloperLink() }
            OnboardingScreenUiEvent.OpenPCSourceCode -> openLink { openLinkContract.openPCSourceCodeLink() }
            OnboardingScreenUiEvent.OpenPrivacyPolicyLink -> openLink { openLinkContract.openPrivacyPolicyLink() }
            OnboardingScreenUiEvent.OpenTOSLink -> openLink { openLinkContract.openTermsOfUseLink() }
            OnboardingScreenUiEvent.RequestNotificationPermission -> sideEffect.tryEmit(OnboardingScreenSideEffect.RequestNotificationPermission)
            OnboardingScreenUiEvent.RequestNearbyDevicesPermission -> sideEffect.tryEmit(OnboardingScreenSideEffect.RequestNearbyDevicesPermission)
            OnboardingScreenUiEvent.RequestOverlayPermission -> requestOverlayPermission()
            OnboardingScreenUiEvent.UpdatePermissionState -> updatePermissionState()
        }
    }

    private fun updatePermissionState() = viewModelScope.launch {
        val permissionState = permissionContract.providePermissionState()
        _state.update {
            it.copy(
                isNotificationGranted = permissionState.isNotificationGranted,
                isNearbyDevicesGranted = permissionState.isNearbyDevicesGranted,
                isOverlayGranted = permissionState.isOverlayGranted
            )
        }
    }

    private fun requestOverlayPermission() = viewModelScope.launch {
        permissionContract.requestOverlayPermission()
    }

    private fun openLink(block: suspend () -> Unit) = viewModelScope.launch {
        block()
    }

    private fun changeTosState(accepted: Boolean) {
        _state.update { it.copy(isTosAccepted = accepted) }
    }

    private fun nextPage() = viewModelScope.launch {
        sideEffect.emit(OnboardingScreenSideEffect.NextPage)
    }

    private fun onboardingFinished() {
        viewModelScope.launch {
            onboardingFinishedContract.onBoardingFinished()
            sendNavigationAction { fromOnboardingScreenToMainScreen() }
        }
    }
}