package com.xxmrk888ytxx.coreandroid

interface Navigator {
    fun fromOnboardingScreenToMainScreen()
    fun fromMainScreenToAddNewDeviceScreen()
    fun fromMainScreenToDeviceConfigurationScreen(deviceId: String)
    fun fromAddNewDeviceScreenToDeviceConfigurationScreen(deviceId: String)
    fun navigateUp()
}