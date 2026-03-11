package com.xxmrk888ytxx.logsscreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.xxmrk888ytxx.logsscreen.model.LogsUiEvent
import com.xxmrk888ytxx.logsscreen.model.ScreenState

@Composable
fun LogsScreen(
    screenState: ScreenState,
    onEvent: (LogsUiEvent) -> Unit
) {
    Text("LogsScreen")
}