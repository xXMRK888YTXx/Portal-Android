package com.xxmrk888ytxx.mainscreen.contract

import com.xxmrk888ytxx.mainscreen.model.DevicesRemovedBannerState
import kotlinx.coroutines.flow.Flow

interface ManageDevicesRemovedBannerStateContract {
    val devicesRemovedBannerState: Flow<DevicesRemovedBannerState>
    suspend fun resetState()
}