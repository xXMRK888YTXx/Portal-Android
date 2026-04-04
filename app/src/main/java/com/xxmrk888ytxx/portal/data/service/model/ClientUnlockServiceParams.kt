package com.xxmrk888ytxx.portal.data.service.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientUnlockServiceParams(
    val clientId: String,
    val tryToRetryUnlockUntilSuccessOrTimeout: Boolean,
    val isSendWOLRequest: Boolean,
    val isSendUnlockRequest: Boolean
) : Parcelable
