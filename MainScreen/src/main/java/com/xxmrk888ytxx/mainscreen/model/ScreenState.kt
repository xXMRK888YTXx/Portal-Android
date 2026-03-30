package com.xxmrk888ytxx.mainscreen.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ScreenState(
    val devices: ImmutableList<Device> = persistentListOf(),
    val isLoading: Boolean = false,
    val dialogState: DialogState = DialogState.Hidden,
    val permissionBannerItemList: List<PermissionBannerItem> = emptyList(),
    val devicesRemovedBannerState:DevicesRemovedBannerState = DevicesRemovedBannerState.None
)
