package com.xxmrk888ytxx.corecompose.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.corecompose.LocalNavigator

fun ComponentActivity.setContentWithThemeAndProviders(
    useDynamicColors: Boolean = true,
    useDarkTheme: Boolean? = null,
    navigator: Navigator,
    content: @Composable () -> Unit,
) {
    setContent {
        CompositionLocalProvider(
            LocalNavigator provides navigator
        ) {
            AppTheme(
                darkTheme = useDarkTheme ?: isSystemInDarkTheme(),
                dynamicColor = useDynamicColors,
                content = content
            )
        }
    }
}