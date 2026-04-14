package com.xxmrk888ytxx.portal.domain

interface LinkManager {
    suspend fun openTermsOfUseLink()
    suspend fun openPrivacyPolicyLink()
    suspend fun openAndroidDeveloperLink()
    suspend fun openPCDeveloperLink()
    suspend fun openAndroidSourceCodeLink()
    suspend fun openPCSourceCodeLink()
}