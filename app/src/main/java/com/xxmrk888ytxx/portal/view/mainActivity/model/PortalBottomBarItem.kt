package com.xxmrk888ytxx.portal.view.mainActivity.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.res.stringResource
import com.xxmrk888ytxx.coreandroid.uiText.UiText
import com.xxmrk888ytxx.coreandroid.uiText.uiText
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.view.mainActivity.view.PortalBottomBar

sealed class PortalBottomBarItem(
    val id: Int,
    val text: UiText,
    @param:DrawableRes val icon: Int
) {
    data object Devices: PortalBottomBarItem(0, uiText(R.string.devices), R.drawable.devices)
    data object Settings: PortalBottomBarItem(1, uiText(R.string.settings), R.drawable.settings)

    companion object {
        val itemList = listOf(Devices, Settings)
    }
}