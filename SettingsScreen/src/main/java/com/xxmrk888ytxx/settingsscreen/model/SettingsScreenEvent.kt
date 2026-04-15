package com.xxmrk888ytxx.settingsscreen.model

import androidx.compose.ui.graphics.Color
import com.xxmrk888ytxx.coreandroid.mvi.UiEvent

sealed interface SettingsScreenEvent : UiEvent {
    data object OnLogsClick : SettingsScreenEvent
    data class OnBiometricProtectionStateChanged(val newState: Boolean) : SettingsScreenEvent
    data class OnAdditionalPasswordAuthStateChanged(val newState: Boolean) : SettingsScreenEvent
    data class OnRemovePairedClientsIfBiometricEnvironmentStateChanged(val newState: Boolean) : SettingsScreenEvent
    data class OnChangeUnsafeUnlockTypesState(val newState: Boolean) : SettingsScreenEvent
    class ConfirmSecurityChanges(val actionAfterConfirm: () -> Unit) : SettingsScreenEvent
    data class OnThemeColorSelected(val newColor: Color?): SettingsScreenEvent
    data object HideBottomSheet : SettingsScreenEvent
    data object OnTermsClicked: SettingsScreenEvent
    data object OnPrivacyClicked: SettingsScreenEvent
    data object OnAndroidSourceCodeClick: SettingsScreenEvent
    data object OnPCSourceCodeClick: SettingsScreenEvent
    data object OnAndroidDeveloperClick: SettingsScreenEvent
    data object OnPCDeveloperClicked: SettingsScreenEvent
    data object OpenOpenSourceLicenses : SettingsScreenEvent
    data object OnThemeClicked : SettingsScreenEvent
    data object OnDownloadPcClientClicked : SettingsScreenEvent

}