package com.xxmrk888ytxx.portal.data.trustManager

import android.annotation.SuppressLint
import com.xxmrk888ytxx.portal.domain.CertificateManager
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@SuppressLint("CustomX509TrustManager")
abstract class PortalTrustManager : X509TrustManager {
    @SuppressLint("TrustAllX509TrustManager")
    override fun checkClientTrusted(
        chain: Array<out X509Certificate?>?,
        authType: String?
    ) {}

    override fun checkServerTrusted(
        chain: Array<out X509Certificate?>?,
        authType: String?
    ) {
        val serverCert = chain?.firstOrNull()
        handleCertificate(serverCert)
    }

    override fun getAcceptedIssuers(): Array<out X509Certificate?>? = arrayOf()

    protected abstract fun handleCertificate(certificate: X509Certificate?)
}