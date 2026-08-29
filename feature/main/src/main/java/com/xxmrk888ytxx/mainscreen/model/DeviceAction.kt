package com.xxmrk888ytxx.mainscreen.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

internal data class DeviceAction(
    @param:StringRes val label: Int,
    @field:DrawableRes val icon: Int,
    val id: Int,
    val onClick: (Device) -> Unit
) {
    companion object {
        const val WAKE_UP_ON_LAN_ID = 0
        const val OPTION_ID = 1
        const val SHORTCUT_ID = 2
    }
}
