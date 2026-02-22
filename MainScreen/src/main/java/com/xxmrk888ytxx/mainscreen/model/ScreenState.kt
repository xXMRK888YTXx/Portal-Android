package com.xxmrk888ytxx.mainscreen.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ScreenState(val devices: ImmutableList<Device> = persistentListOf())
