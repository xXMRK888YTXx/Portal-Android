package com.xxmrk888ytxx.mainscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface MainScreenEvent : UiEvent {
    data object AddNewDevice : MainScreenEvent
    data class SendUnlockRequest(val device: Device) : MainScreenEvent
    data class ToDeviceDetailsScreen(val deviceId: String) : MainScreenEvent
    data class ShowCreateShortcutModelDialog(val device: Device) : MainScreenEvent
    data object DismissDialog : MainScreenEvent
    data object CreateShortcut : MainScreenEvent
    data object RequestNearbyDevicesPermission : MainScreenEvent
    data object RequestNotificationPermission : MainScreenEvent
    data object RequestFullScreenIntentPermission : MainScreenEvent
    data class PermissionGranted(val permission: Permission) : MainScreenEvent
    data class OnIsRequiredBiometricUnlockStateChanged(val isRequiredBiometricUnlock: Boolean) : MainScreenEvent
    data object ActivityInOnResumeState : MainScreenEvent
    data object DismissDevicesRemovedBanner : MainScreenEvent
    data class WakeUpOnLANClicked(val device: Device) : MainScreenEvent
    data class OnMacAddressChanged(val newText: String) : MainScreenEvent
    data class OnIsTryToSendEnabledChanged(val newState: Boolean): MainScreenEvent
    data class OnIsRequiredSendWOLRequestChanged(val newValue: Boolean) : MainScreenEvent
    data object SendWOLRequest : MainScreenEvent
    data object SaveWOLMacAddress : MainScreenEvent
}