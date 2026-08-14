package com.xxmrk888ytxx.corecompose.uiText

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.xxmrk888ytxx.coreandroid.uiText.UiText
import com.xxmrk888ytxx.coreandroid.uiText.asString

@Composable
fun UiText.asString(): String {
    val context = LocalContext.current
    return asString(context)
}