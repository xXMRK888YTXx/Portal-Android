package com.xxmrk888ytxx.addnewdevicescreen.model

sealed class ScreenState() {
    object NoSelectedType : ScreenState()

    data class Wifi(
        val host: String = "",
        val pairCode: String = ""
    ) : ScreenState()

    data class Bluetooth(
        val pairCode: String = ""
    ) : ScreenState()
}
