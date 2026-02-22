package com.xxmrk888ytxx.portal.domain.model

data class Device(
    val deviceId: String,
    val deviceName: String,
    val host: String,
    val clientCertificate: Certificate,
    val serverCertificateFingerprint: String,
)
