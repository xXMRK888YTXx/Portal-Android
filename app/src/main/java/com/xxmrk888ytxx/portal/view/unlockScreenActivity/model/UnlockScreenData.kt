package com.xxmrk888ytxx.portal.view.unlockScreenActivity.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnlockScreenData(
    val clientId: String,
    val requestId: String?,
    val decisionId: String? = null,
) : Parcelable
