package com.xxmrk888ytxx.settingsscreen

import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.settingsscreen.contract.ProvideSettingsState
import com.xxmrk888ytxx.settingsscreen.model.ScreenState
import com.xxmrk888ytxx.settingsscreen.model.SettingsScreenEvent
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val provideSettingsState: ProvideSettingsState
) : SideEffectPortalViewModel<ScreenState, SettingsScreenEvent>(
    ScreenState()
) {
    override val state: StateFlow<ScreenState> = combine<Any, ScreenState>(
        provideSettingsState.isBiometricProtectionEnabled,
        provideSettingsState.appVersion
    ) { flowArray ->
        val isBiometricProtectionEnabled = flowArray[0] as Boolean
        val appVersion = flowArray[1] as String
        ScreenState(
            isBiometricProtectionEnabled = isBiometricProtectionEnabled,
            appVersion = appVersion
        )
    }.stateWhileSubscribed()

    override fun handleEvent(event: SettingsScreenEvent) {
        when(event) {
            SettingsScreenEvent.OnLogsClick -> sendNavigationAction { fromSettingsScreenToLogsScreen() }
        }
    }

}