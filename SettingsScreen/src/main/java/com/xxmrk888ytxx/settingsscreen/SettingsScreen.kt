package com.xxmrk888ytxx.settingsscreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.settingsscreen.model.ScreenState
import com.xxmrk888ytxx.settingsscreen.model.SettingsScreenEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun SettingsScreen(
    screenState: ScreenState,
    onEvent: (SettingsScreenEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    Text("SettingsScreen")
}