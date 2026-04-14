package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.onboardingscreen.contract.OnboardingFinishedContract
import com.xxmrk888ytxx.onboardingscreen.contract.OpenLinkContract
import com.xxmrk888ytxx.onboardingscreen.contract.PermissionContract
import com.xxmrk888ytxx.portal.providedContract.onboardingScreen.OnboardingFinishedContractImpl
import com.xxmrk888ytxx.portal.providedContract.onboardingScreen.OpenLinkContractImpl
import com.xxmrk888ytxx.portal.providedContract.onboardingScreen.PermissionContractImpl
import dagger.Binds
import dagger.Module

@Module
interface OnboardingScreenModule {
    @Binds
    fun bindOnboardingFinishedContract(onboardingFinishedContract: OnboardingFinishedContractImpl): OnboardingFinishedContract

    @Binds
    fun bindsOpenLinkContract(
        openLinkContractImpl: OpenLinkContractImpl
    ) : OpenLinkContract

    @Binds
    fun bindsPermissionContract(
        permissionContractImpl: PermissionContractImpl
    ) : PermissionContract
}