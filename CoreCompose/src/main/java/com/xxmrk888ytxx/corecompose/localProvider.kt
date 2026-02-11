package com.xxmrk888ytxx.corecompose

import androidx.compose.runtime.compositionLocalOf
import com.xxmrk888ytxx.coreandroid.Navigator

val LocalNavigator = compositionLocalOf<Navigator> { error("LocalNavigator not provided") }