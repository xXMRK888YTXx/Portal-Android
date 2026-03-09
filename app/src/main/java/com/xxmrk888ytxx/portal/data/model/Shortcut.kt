package com.xxmrk888ytxx.portal.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Shortcut(
    val shortcutId: String,
    val clientId: String,
    val isRequiredBiometricUnlock: Boolean,
) : Parcelable {}
