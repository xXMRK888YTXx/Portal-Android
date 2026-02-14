package com.xxmrk888ytxx.portal.providedContract.onboardingScreen

import com.xxmrk888ytxx.onboardingscreen.contract.OnboardingFinishedContract
import com.xxmrk888ytxx.portal.domain.PreferenceManager
import javax.inject.Inject

class OnboardingFinishedContractImpl @Inject constructor(
    private val preferenceManager: PreferenceManager
) : OnboardingFinishedContract {
    override suspend fun onBoardingFinished() {
        preferenceManager.markOnboardingAsPassed()
    }
}