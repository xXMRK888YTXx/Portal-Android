package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.model.Certificate
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.cert.X509Certificate
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CertificateManagerImpl @Inject constructor() : CertificateManager {

    override fun createNewCertificate(): Certificate {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()

        val issuer = X500Name("CN=PORTAL, O=xXTeam, C=BY")
        val subject = issuer

        val serialNumber = BigInteger.valueOf(System.currentTimeMillis())

        val notBefore = Date()
        val notAfter = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365 * 25))

        val certBuilder = JcaX509v3CertificateBuilder(
            /* issuer = */ issuer,
            /* serial = */ serialNumber,
            /* notBefore = */ notBefore,
            /* notAfter = */ notAfter,
            /* subject = */ subject,
            /* publicKey = */ keyPair.public
        )

        val contentSigner = JcaContentSignerBuilder("SHA256WithRSAEncryption")
            .build(keyPair.private)

        val certHolder = certBuilder.build(contentSigner)

        val certificate = JcaX509CertificateConverter()
            .setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider())
            .getCertificate(certHolder)

        return Certificate(keyPair, certificate)
    }

    override fun getX509CertificateFingerprint(x509certificate: X509Certificate): String {
        val md = MessageDigest.getInstance("SHA-256")
        val der = x509certificate.encoded
        val digest = md.digest(der)
        return digest.joinToString(":") { byte ->
            "%02X".format(byte)
        }
    }
}