package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.Certificate
import java.security.cert.X509Certificate

interface CertificateManager {
    fun createNewCertificate(): Certificate
    fun getX509CertificateFingerprint(x509certificate: X509Certificate) : String
}