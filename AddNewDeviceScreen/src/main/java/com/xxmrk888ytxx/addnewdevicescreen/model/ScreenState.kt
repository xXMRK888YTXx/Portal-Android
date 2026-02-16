package com.xxmrk888ytxx.addnewdevicescreen.model

sealed class ScreenState(open val isLoading: Boolean) {
    object NoSelectedType : ScreenState(false)

    data class Wifi(
        val host: String = "",
        val pairCode: String = "",
        val isDataValid: Boolean = false,
        override val isLoading: Boolean = false,
    ) : ScreenState(isLoading)

    data class Bluetooth(
        val pairCode: String = "",
        override val isLoading: Boolean = false,
    ) : ScreenState(isLoading)
}
