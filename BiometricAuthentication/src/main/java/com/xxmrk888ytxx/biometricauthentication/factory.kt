package com.xxmrk888ytxx.biometricauthentication

import android.content.Context

fun BiometricAuthManager.Companion.create(context: Context): BiometricAuthManager =
    BiometricAuthManagerImpl(context)