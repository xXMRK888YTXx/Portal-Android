package com.xxmrk888ytxx.mainscreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.ScreenState

@Composable
fun MainScreen(
    screenState: ScreenState,
    onEvent: (MainScreenEvent) -> Unit,
) {
    Text("MainScreen")
}