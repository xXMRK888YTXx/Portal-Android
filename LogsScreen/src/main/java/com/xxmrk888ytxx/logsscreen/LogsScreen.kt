package com.xxmrk888ytxx.logsscreen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.coreandroid.AndroidLogger
import com.xxmrk888ytxx.logsscreen.model.LogsUiEvent
import com.xxmrk888ytxx.logsscreen.model.ScreenState

@Composable
fun LogsScreen(
    screenState: ScreenState,
    onEvent: (LogsUiEvent) -> Unit
) {
    Scaffold(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentWindowInsets = WindowInsets(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    AndroidLogger.clearLogs()
                }
            ) {
                Text("Clear")
            }
        }
    ) { paddingValues ->
        LazyColumn(Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            items(screenState.logsStrings, key = { it.hashCode() }) {
                Text(text = it)
            }
        }
    }
}