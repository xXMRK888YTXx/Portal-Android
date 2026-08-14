package com.xxmrk888ytxx.addnewdevicescreen.model

data class ScanQrCodeResult(
    val deviceName: String,
    val host: String?,
    val macAddress: String?,
    val pairCode: Int,
)
