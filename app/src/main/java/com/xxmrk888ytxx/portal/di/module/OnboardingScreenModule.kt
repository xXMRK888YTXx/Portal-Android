package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.onboardingscreen.contract.OnboardingFinishedContract
import com.xxmrk888ytxx.portal.providedContract.onboardingScreen.OnboardingFinishedContractImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface OnboardingScreenModule {
    @Binds
    fun bindOnboardingFinishedContract(onboardingFinishedContract: OnboardingFinishedContractImpl): OnboardingFinishedContract
}