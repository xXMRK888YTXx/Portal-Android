package com.xxmrk888ytxx.portal.data.trustManager

import android.annotation.SuppressLint
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.exception.BadServerCertificateException
import java.security.cert.X509Certificate

@SuppressLint("CustomX509TrustManager")
class TrustManagerByServerCertificateHash(
    private val certificateManager: CertificateManager,
    private val expectedServerHash: String
) : PortalTrustManager() {
    override fun handleCertificate(certificate: X509Certificate?) {
        if (certificate == null) throw IllegalArgumentException("Server not provided certificate")
        val serverCertificateHash = certificateManager.getX509CertificateFingerprint(certificate)
        if (serverCertificateHash != expectedServerHash) throw BadServerCertificateException("Server certificate is not match with trust certificate." +
                "ServerCertificateFingerprint: $serverCertificateHash, trusted: $expectedServerHash")
    }
}