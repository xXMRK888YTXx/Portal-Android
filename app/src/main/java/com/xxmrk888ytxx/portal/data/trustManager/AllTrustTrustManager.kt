package com.xxmrk888ytxx.portal.data.trustManager

import android.annotation.SuppressLint
import java.security.cert.X509Certificate

@SuppressLint("CustomX509TrustManager")
class AllTrustTrustManager : PortalTrustManager() {
    override fun handleCertificate(certificate: X509Certificate?) {
        if(certificate == null) { "Server not provided certificate" }
    }
}