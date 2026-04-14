package com.xxmrk888ytxx.deviceconfigurationscreen.contract

import kotlinx.coroutines.flow.Flow

interface UnsafeMethodAvailableStateProvider {
    val isDisabled: Flow<Boolean>
}