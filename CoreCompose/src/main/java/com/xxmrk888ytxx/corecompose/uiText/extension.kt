package com.xxmrk888ytxx.corecompose.uiText

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.xxmrk888ytxx.coreandroid.uiText.UiText

@Composable
fun UiText.asString(): String = when(this) {
    is UiText.ResourceValue -> stringResource(resId)
    is UiText.StringValue -> value
}