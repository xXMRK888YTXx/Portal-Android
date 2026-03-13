package com.xxmrk888ytxx.addnewdevicescreen.model

import androidx.core.text.isDigitsOnly
import com.xxmrk888ytxx.coreandroid.DefaultValidator

internal object Validator {

    fun isDeviceNameValid(deviceName: String) : Boolean = deviceName.isNotEmpty()

    fun isHostValid(host: String) : Boolean = DefaultValidator.isHostValid(host)

    fun isPairCodeValid(code: String) : Boolean = code.isDigitsOnly() && code.length == 6

    fun isWifiStateValid(state: ScreenState.Wifi): Boolean = isDeviceNameValid(state.deviceName) && isHostValid(state.host) && isPairCodeValid(state.pairCode)
}