package com.xxmrk888ytxx.settingsscreen.model

import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface SettingsScreenEvent : UiEvent {
    data object OnLogsClick : SettingsScreenEvent
    data class OnBiometricProtectionStateChanged(val newState: Boolean) : SettingsScreenEvent
    data class OnAdditionalPasswordAuthStateChanged(val newState: Boolean) : SettingsScreenEvent
    data class OnRemovePairedClientsIfBiometricEnvironmentStateChanged(val newState: Boolean) : SettingsScreenEvent
    data class OnChangeUnsafeUnlockTypesState(val newState: Boolean) : SettingsScreenEvent
    class ConfirmSecurityChanges(val actionAfterConfirm: () -> Unit) : SettingsScreenEvent
    data object HideBottomSheet : SettingsScreenEvent
}