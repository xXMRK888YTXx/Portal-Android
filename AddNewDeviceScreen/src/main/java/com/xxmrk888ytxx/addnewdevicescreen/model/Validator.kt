package com.xxmrk888ytxx.addnewdevicescreen.model

import androidx.core.text.isDigitsOnly

internal object Validator {

    fun isDeviceNameValid(deviceName: String) : Boolean = deviceName.isNotEmpty()

    fun isHostValid(host: String) : Boolean {
        val regexIPV4 = "^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])$".toRegex()
        return host.isNotEmpty() && regexIPV4.matches(host)
    }

    fun isPairCodeValid(code: String) : Boolean = code.isDigitsOnly() && code.length == 6

    fun isWifiStateValid(state: ScreenState.Wifi): Boolean = isDeviceNameValid(state.deviceName) && isHostValid(state.host) && isPairCodeValid(state.pairCode)
}