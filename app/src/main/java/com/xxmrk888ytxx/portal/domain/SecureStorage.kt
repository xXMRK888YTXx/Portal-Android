package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.Certificate

interface SecureStorage {
    suspend fun saveCertificateByAlias(alias: String, certificate: Certificate)
    suspend fun restoreCertificateByAlias(alias: String): Certificate
}