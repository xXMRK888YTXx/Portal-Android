package com.xxmrk888ytxx.biometricauthentication.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.xxmrk888ytxx.biometricauthentication.BiometricAuthManager
import com.xxmrk888ytxx.biometricauthentication.create

@Composable
fun rememberBiometricAuthManager(): BiometricAuthManager {
    val context = LocalContext.current.applicationContext
    return remember { BiometricAuthManager.create(context) }
}