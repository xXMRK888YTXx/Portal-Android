package com.xxmrk888ytxx.corecompose

import androidx.compose.runtime.compositionLocalOf
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.ToastManager

val LocalNavigator = compositionLocalOf<Navigator> { error("LocalNavigator not provided") }
val LocalToastManager = compositionLocalOf<ToastManager> { error("LocalToastManager not provided") }