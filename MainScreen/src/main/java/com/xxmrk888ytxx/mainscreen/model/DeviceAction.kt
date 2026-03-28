package com.xxmrk888ytxx.mainscreen.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

internal data class DeviceAction(
    @param:StringRes val label: Int,
    @field:DrawableRes val icon: Int,
    val enabled: Boolean,
    val onClick: () -> Unit
)
