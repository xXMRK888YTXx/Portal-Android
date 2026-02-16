package com.xxmrk888ytxx.portal.domain.model

import java.security.KeyPair
import java.security.cert.X509Certificate

data class Certificate(
    val keyPair: KeyPair,
    val certificate: X509Certificate
)