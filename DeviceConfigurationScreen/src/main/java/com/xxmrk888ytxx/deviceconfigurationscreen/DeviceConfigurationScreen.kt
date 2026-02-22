package com.xxmrk888ytxx.deviceconfigurationscreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationUiEvent
import com.xxmrk888ytxx.deviceconfigurationscreen.model.ScreenState
import kotlinx.coroutines.flow.Flow

@Composable
fun DeviceConfigurationScreen(
    screenState: ScreenState,
    onEvent: (DeviceConfigurationUiEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    Text("DeviceConfigurationScreen")
}