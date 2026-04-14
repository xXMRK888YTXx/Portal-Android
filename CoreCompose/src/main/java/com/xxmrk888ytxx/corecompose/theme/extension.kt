package com.xxmrk888ytxx.corecompose.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.ToastManager
import com.xxmrk888ytxx.corecompose.LocalNavigator
import com.xxmrk888ytxx.corecompose.LocalToastManager
import kotlinx.coroutines.flow.StateFlow

fun ComponentActivity.setContentWithThemeAndProviders(
    useDarkTheme: Boolean? = null,
    navigator: Navigator,
    toastManager: ToastManager,
    themeColor: StateFlow<Color?>,
    content: @Composable () -> Unit,
) {
    setContent {
        val themeColor by themeColor.collectAsState()
        val randomColor = remember(themeColor) {
            AppSeedColors.allColors.random()
        }

        CompositionLocalProvider(
            LocalNavigator provides navigator,
            LocalToastManager provides toastManager,
        ) {
            AppTheme(
                darkTheme = useDarkTheme ?: isSystemInDarkTheme(),
                seedColor = if (AppSeedColors.RandomColor == themeColor) randomColor else themeColor,
                content = content
            )
        }
    }
}