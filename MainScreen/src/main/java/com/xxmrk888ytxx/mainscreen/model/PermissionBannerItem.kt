package com.xxmrk888ytxx.mainscreen.model

import androidx.annotation.DrawableRes
import com.xxmrk888ytxx.coreandroid.uiText.UiText

data class PermissionBannerItem(
    val title: UiText,
    val description: UiText,
    @param:DrawableRes val iconRes: Int,
    val eventForRequestPermission: MainScreenEvent
)