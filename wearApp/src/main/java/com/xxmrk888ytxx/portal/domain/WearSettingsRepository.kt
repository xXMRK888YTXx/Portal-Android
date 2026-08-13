package com.xxmrk888ytxx.portal.domain

import kotlinx.coroutines.flow.StateFlow

interface WearSettingsRepository {
    val showRequestsOnLockedScreen: StateFlow<Boolean>
    fun setShowRequestsOnLockedScreen(value: Boolean)
}
