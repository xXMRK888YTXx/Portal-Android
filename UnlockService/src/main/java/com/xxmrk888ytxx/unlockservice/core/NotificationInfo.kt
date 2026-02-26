package com.xxmrk888ytxx.unlockservice.core

import androidx.annotation.IntRange
import androidx.annotation.StringRes

internal data class NotificationInfo(
    @field:IntRange(1, Int.MAX_VALUE.toLong())
    val id: Int,
    @field:StringRes
    val textResId: Int
)
