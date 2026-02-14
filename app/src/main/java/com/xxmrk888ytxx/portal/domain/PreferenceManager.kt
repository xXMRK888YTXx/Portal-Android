package com.xxmrk888ytxx.portal.domain

import kotlinx.coroutines.flow.Flow

interface PreferenceManager {
    val isOnboardingPassed: Flow<Boolean>
    suspend fun markOnboardingAsPassed()
}