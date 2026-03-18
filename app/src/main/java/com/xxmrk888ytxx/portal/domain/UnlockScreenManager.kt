package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.WifiDevice

interface UnlockScreenManager {
    fun showUnlockScreen(wifiDevice: WifiDevice)
}