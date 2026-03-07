package com.xxmrk888ytxx.biometricauthentication

import android.content.Context

fun BiometricAuthManager.create(context: Context): BiometricAuthManager =
    BiometricAuthManagerImpl(context)