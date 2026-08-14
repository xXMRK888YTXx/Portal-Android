package com.xxmrk888ytxx.mainscreen.contract

import com.xxmrk888ytxx.mainscreen.model.Device
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProvideSavedDevices {
    val devices: Flow<ImmutableList<Device>>
}