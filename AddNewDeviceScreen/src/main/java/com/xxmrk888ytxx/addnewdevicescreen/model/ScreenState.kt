package com.xxmrk888ytxx.addnewdevicescreen.model

sealed class ScreenState(open val isLoading: Boolean, open val deviceName: String) {
    object NoSelectedType : ScreenState(false, "")

    data class Wifi(
        val host: String = "",
        val pairCode: String = "",
        val isDataValid: Boolean = false,
        override val isLoading: Boolean = false,
        override val deviceName: String = ""
    ) : ScreenState(isLoading, deviceName)

    data class Bluetooth(
        val pairCode: String = "",
        override val isLoading: Boolean = false,
        override val deviceName: String = ""
    ) : ScreenState(isLoading, deviceName)
}
