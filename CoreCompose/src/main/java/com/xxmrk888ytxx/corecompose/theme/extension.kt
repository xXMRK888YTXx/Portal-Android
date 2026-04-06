package com.xxmrk888ytxx.corecompose.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.corecompose.LocalNavigator
import com.xxmrk888ytxx.corecompose.LocalToastManager

fun ComponentActivity.setContentWithThemeAndProviders(
    useDynamicColors: Boolean = true,
    useDarkTheme: Boolean? = null,
    navigator: Navigator,
    toastManager: ToastManager,
    content: @Composable () -> Unit,
) {
    setContent {
        CompositionLocalProvider(
            LocalNavigator provides navigator,
            LocalToastManager provides toastManager,
        ) {
            AppTheme(
                darkTheme = useDarkTheme ?: isSystemInDarkTheme(),
                seedColor = null,
                content = content
            )
        }
    }
}