package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.Device

interface UnlockScreenManager {
    fun showUnlockScreen(device: Device)
}