package com.xxmrk888ytxx.mainscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface MainScreenEvent : UiEvent {
    data object AddNewDevice : MainScreenEvent
    data class SendUnlockRequest(val device: Device) : MainScreenEvent
    data class ToDeviceDetailsScreen(val deviceId: String) : MainScreenEvent
    data class ShowCreateShortcutModelDialog(val device: Device) : MainScreenEvent
    data object DismissCreateShortcutModelDialog : MainScreenEvent
    data object CreateShortcut : MainScreenEvent
    data class OnIsRequiredBiometricUnlockStateChanged(val isRequiredBiometricUnlock: Boolean) : MainScreenEvent
}