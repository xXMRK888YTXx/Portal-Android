package com.xxmrk888ytxx.settingsscreen

import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.settingsscreen.contract.ChangeSettingsContract
import com.xxmrk888ytxx.settingsscreen.contract.ProvideSettingsState
import com.xxmrk888ytxx.settingsscreen.model.ScreenState
import com.xxmrk888ytxx.settingsscreen.model.SettingsScreenEvent
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val provideSettingsState: ProvideSettingsState,
    private val changeSettingsContract: ChangeSettingsContract
) : SideEffectPortalViewModel<ScreenState, SettingsScreenEvent>(
    ScreenState()
) {
    override val state: StateFlow<ScreenState> = combine<Any, ScreenState>(
        provideSettingsState.isBiometricProtectionEnabled,
        provideSettingsState.appVersion,
        provideSettingsState.isAdditionalPasswordAuthEnabled,
        provideSettingsState.isRemovePairedClientsIfBiometricEnvironmentChangedEnabled,
    ) { flowArray ->
        val isBiometricProtectionEnabled = flowArray[0] as Boolean
        val appVersion = flowArray[1] as String
        val isAdditionalPasswordAuthEnabled = flowArray[2] as Boolean
        val isRemovePairedClientsIfBiometricEnvironmentChangedEnabled = flowArray[3] as Boolean

        ScreenState(
            isBiometricProtectionEnabled = isBiometricProtectionEnabled,
            appVersion = appVersion,
            isAdditionalPasswordAuthEnabled = isAdditionalPasswordAuthEnabled,
            isRemovePairedClientsIfBiometricEnvironmentChangedEnabled = isRemovePairedClientsIfBiometricEnvironmentChangedEnabled,
        )
    }.stateWhileSubscribed()

    override fun handleEvent(event: SettingsScreenEvent) {
        when (event) {
            SettingsScreenEvent.OnLogsClick -> sendNavigationAction { fromSettingsScreenToLogsScreen() }
            is SettingsScreenEvent.OnAdditionalPasswordAuthStateChanged -> changeAdditionalPasswordAuthState(
                event.newState
            )

            is SettingsScreenEvent.OnBiometricProtectionStateChanged -> changeBiometricAuthState(
                event.newState
            )

            is SettingsScreenEvent.OnRemovePairedClientsIfBiometricEnvironmentStateChanged -> changeRemovePairedClientsIfBiometricEnvironmentChangedState(
                event.newState
            )
        }
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