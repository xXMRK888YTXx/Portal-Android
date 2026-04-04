package com.xxmrk888ytxx.portal.providedContract.onboardingScreen

import com.xxmrk888ytxx.onboardingscreen.contract.OnboardingFinishedContract
import com.xxmrk888ytxx.portal.domain.SettingsRepository
import javax.inject.Inject

class OnboardingFinishedContractImpl @Inject constructor(
    private val settingsRepository: SettingsRepository
) : OnboardingFinishedContract {
    override suspend fun onBoardingFinished() {
        //TODO Remove return
        return
        settingsRepository.markOnboardingAsPassed()
    }
}