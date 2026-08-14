package com.xxmrk888ytxx.addnewdevicescreen.model

enum class Page(internal val id: Int) {
    SELECT_TYPE(0), CONFIGURATION_WIFI(1), CONFIGURATION_BLUETOOTH(2);

    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.id == value } ?: throw IllegalArgumentException("Cannot find page with id $value")
    }
}