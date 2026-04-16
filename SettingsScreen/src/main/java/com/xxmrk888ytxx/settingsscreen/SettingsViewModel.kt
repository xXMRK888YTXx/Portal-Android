package com.xxmrk888ytxx.settingsscreen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.settingsscreen.contract.BiometricProtectionAvailableStateProvider
import com.xxmrk888ytxx.settingsscreen.contract.ChangeSettingsContract
import com.xxmrk888ytxx.settingsscreen.contract.OpenLinkContract
import com.xxmrk888ytxx.settingsscreen.contract.ProvideSettingsState
import com.xxmrk888ytxx.settingsscreen.model.BottomSheetState
import com.xxmrk888ytxx.settingsscreen.model.ScreenState
import com.xxmrk888ytxx.settingsscreen.model.SettingsScreenEvent
import com.xxmrk888ytxx.settingsscreen.model.SettingsScreenSideEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val provideSettingsState: ProvideSettingsState,
    private val changeSettingsContract: ChangeSettingsContract,
    private val biometricProtectionAvailableStateProvider: BiometricProtectionAvailableStateProvider,
    private val openLinkContract: OpenLinkContract
) : SideEffectPortalViewModel<ScreenState, SettingsScreenEvent>(
    ScreenState()
) {

    private val bottomSheetState = MutableStateFlow<BottomSheetState>(BottomSheetState.None)

    override val state: StateFlow<ScreenState> = combine<Any, ScreenState>(
        provideSettingsState.isBiometricProtectionEnabled,
        provideSettingsState.appVersion,
        provideSettingsState.isAdditionalPasswordAuthEnabled,
        provideSettingsState.isRemovePairedClientsIfBiometricEnvironmentChangedEnabled,
        bottomSheetState,
        provideSettingsState.isUnsafeUnlockTypesDisabled,
        biometricProtectionAvailableStateProvider.isAvailable
    ) { flowArray ->
        val isBiometricProtectionEnabled = flowArray[0] as Boolean
        val appVersion = flowArray[1] as String
        val isAdditionalPasswordAuthEnabled = flowArray[2] as Boolean
        val isRemovePairedClientsIfBiometricEnvironmentChangedEnabled = flowArray[3] as Boolean
        val bottomSheetState = flowArray[4] as BottomSheetState
        val isUnsafeUnlockTypesDisabled = flowArray[5] as Boolean
        val isBiometricAuthAvailable = flowArray[6] as Boolean

        ScreenState(
            bottomSheetState = bottomSheetState,
            isBiometricProtectionEnabled = isBiometricProtectionEnabled,
            appVersion = appVersion,
            isAdditionalPasswordAuthEnabled = isAdditionalPasswordAuthEnabled,
            isRemovePairedClientsIfBiometricEnvironmentChangedEnabled = isRemovePairedClientsIfBiometricEnvironmentChangedEnabled,
            isUnsafeUnlockTypesDisabled = isUnsafeUnlockTypesDisabled,
            isBiometricAuthAvailable = isBiometricAuthAvailable
        )
    }.stateWhileSubscribed()

    override fun handleEvent(event: SettingsScreenEvent) {
        when (event) {
            SettingsScreenEvent.OnLogsClick -> sendNavigationAction { fromSettingsScreenToLogsScreen() }

            is SettingsScreenEvent.OnBiometricProtectionStateChanged -> {
                bottomSheetState.value = BottomSheetState.ConfirmSecurityChangesDialog(isForEnablingSetting = event.newState) {
                    changeBiometricAuthState(event.newState)
                }
            }

            is SettingsScreenEvent.OnAdditionalPasswordAuthStateChanged -> changeAdditionalPasswordAuthState(
                event.newState
            )

            is SettingsScreenEvent.OnRemovePairedClientsIfBiometricEnvironmentStateChanged -> bottomSheetState.value = BottomSheetState.ConfirmSecurityChangesDialog(isForEnablingSetting = event.newState) {
                changeRemovePairedClientsIfBiometricEnvironmentChangedState(event.newState)
            }

            is SettingsScreenEvent.ConfirmSecurityChanges -> event.actionAfterConfirm()
            SettingsScreenEvent.HideBottomSheet -> bottomSheetState.value = BottomSheetState.None
            is SettingsScreenEvent.OnChangeUnsafeUnlockTypesState -> bottomSheetState.value = BottomSheetState.ConfirmSecurityChangesDialog(isForEnablingSetting = event.newState) {
                changeUnsafeUnlockTypesState(event.newState)
            }

            SettingsScreenEvent.OnAndroidDeveloperClick -> openLink { openLinkContract.openAndroidDeveloperLink() }
            SettingsScreenEvent.OnPCDeveloperClicked -> openLink { openLinkContract.openPCDeveloperLink()}
            SettingsScreenEvent.OnPrivacyClicked -> openLink { openLinkContract.openPrivacyPolicyLink() }
            SettingsScreenEvent.OnAndroidSourceCodeClick -> openLink { openLinkContract.openAndroidSourceCodeLink() }
            SettingsScreenEvent.OnTermsClicked -> openLink { openLinkContract.openTermsOfUseLink() }
            SettingsScreenEvent.OnPCSourceCodeClick -> openLink { openLinkContract.openPCSourceCodeLink() }
            SettingsScreenEvent.OpenOpenSourceLicenses -> sideEffect.tryEmit(
                SettingsScreenSideEffect.OpenOpenSourceLicenses)
            SettingsScreenEvent.OnThemeClicked -> showSelectThemeDialog()
            is SettingsScreenEvent.OnThemeColorSelected -> changeThemeColor(event.newColor)
            SettingsScreenEvent.OnDownloadPcClientClicked -> openLink { openLinkContract.openPCClientDownloadLink() }
            SettingsScreenEvent.OpenTroubleshootingLink -> openLink { openLinkContract.openTroubleshootingLink() }
        }
    }

    private fun changeThemeColor(newColor: Color?) = viewModelScope.launch {
        changeSettingsContract.updateThemeColor(newColor)
    }

    private fun showSelectThemeDialog() = viewModelScope.launch {
        fastDebugLog(provideSettingsState.themeColor.first())
        bottomSheetState.value = BottomSheetState.SelectThemeDialog(provideSettingsState.themeColor.first())
    }

    private fun openLink(block: suspend () -> Unit) = viewModelScope.launch {
        block()
    }

    private fun changeUnsafeUnlockTypesState(newState: Boolean) = viewModelScope.launch {
        changeSettingsContract.updateUnsafeUnlockTypesState(newState)
    }

    private fun changeBiometricAuthState(isEnabled: Boolean) = viewModelScope.launch {
        changeSettingsContract.updateBiometricProtectionState(isEnabled)
    }

    private fun changeAdditionalPasswordAuthState(isEnabled: Boolean) = viewModelScope.launch {
        changeSettingsContract.updateAdditionalPasswordAuthState(isEnabled)
    }

    private fun changeRemovePairedClientsIfBiometricEnvironmentChangedState(isEnabled: Boolean) =
        viewModelScope.launch {
            changeSettingsContract.updateRemovePairedClientsIfBiometricEnvironmentChangedState(
                isEnabled
            )
        }


}