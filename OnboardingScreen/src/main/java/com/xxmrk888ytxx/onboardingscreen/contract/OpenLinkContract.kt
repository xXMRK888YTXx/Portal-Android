package com.xxmrk888ytxx.onboardingscreen.contract

interface OpenLinkContract {
    suspend fun openTermsOfUseLink()
    suspend fun openPrivacyPolicyLink()
    suspend fun openPCSourceCodeLink()
    suspend fun openAndroidDeveloperLink()
    suspend fun openPCDeveloperLink()
    suspend fun openAndroidSourceCodeLink()
}