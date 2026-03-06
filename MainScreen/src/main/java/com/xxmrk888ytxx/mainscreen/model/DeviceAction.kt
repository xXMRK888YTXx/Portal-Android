package com.xxmrk888ytxx.mainscreen.model

import androidx.annotation.DrawableRes

internal data class DeviceAction(
    val label: String,
    @field:DrawableRes val icon: Int,
    val enabled: Boolean,
    val onClick: () -> Unit
)
