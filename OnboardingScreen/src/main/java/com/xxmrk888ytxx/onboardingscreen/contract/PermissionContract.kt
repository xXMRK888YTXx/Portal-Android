package com.xxmrk888ytxx.onboardingscreen.contract

import com.xxmrk888ytxx.onboardingscreen.model.PermissionState

interface PermissionContract {
    suspend fun providePermissionState(): PermissionState
    suspend fun requestOverlayPermission()
    suspend fun requestIgnoreBatteryOptimization()
}