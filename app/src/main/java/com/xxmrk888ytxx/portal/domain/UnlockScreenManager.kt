package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest
import com.xxmrk888ytxx.portal.domain.model.WifiDevice

interface UnlockScreenManager {
    fun showUnlockScreen(wifiDevice: WifiDevice, request: UnlockServiceRequest)
    fun showUnlockScreen(bluetoothDevice: BluetoothDevice, request: UnlockServiceRequest)
}