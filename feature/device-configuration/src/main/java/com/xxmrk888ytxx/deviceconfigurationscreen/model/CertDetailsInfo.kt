package com.xxmrk888ytxx.deviceconfigurationscreen.model

enum class IdentifierType {
    SHA_256,
    UUID_V4
}

data class CertDetailsInfo(
    val title: String,
    val type: IdentifierType,
    val value: String,
    val description: String? = null
)

enum class ActiveEditDialog {
    DEVICE_NAME,
    HOST_IP,
    WOL_MAC
}
