package com.xxmrk888ytxx.corecompose.theme
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.rememberDynamicColorScheme

@Composable
fun AppTheme(
    seedColor: Color? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        // Condition 1: User provided a specific color
        seedColor != null -> {
            rememberDynamicColorScheme(
                seedColor = seedColor,
                isDark = darkTheme,
            )
        }

        // Condition 2: No specific color, but system dynamic color is available (Android 12+)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // Condition 3: Fallback to the standard default color
        else -> {
            rememberDynamicColorScheme(
                seedColor = AppSeedColors.DeepAmethyst,
                isDark = darkTheme,
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

