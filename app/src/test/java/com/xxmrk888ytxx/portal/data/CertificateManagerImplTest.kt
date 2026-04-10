package com.xxmrk888ytxx.portal.data

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.security.Security

class CertificateManagerImplTest {

    private val certificateManager = CertificateManagerImpl()

    @Before
    fun setup() {
        // Add BouncyCastleProvider as it is used in CertificateManagerImpl
        Security.removeProvider("BC")
        Security.addProvider(BouncyCastleProvider())
    }

    @Test
    fun `createNewCertificate should generate a valid certificate and keypair`() {
        val result = certificateManager.createNewCertificate()
        
        assertNotNull(result)
        assertNotNull(result.keyPair)
        assertNotNull(result.x509Certificate)
        
        // Check the issuer (contains required components)
        val issuerName = result.x509Certificate.issuerX500Principal.name
        assertTrue(issuerName.contains("CN=PORTAL"))
        assertTrue(issuerName.contains("O=xXTeam"))
        assertTrue(issuerName.contains("C=BY"))
        
        // Check the algorithm
        assertEquals("RSA", result.keyPair.public.algorithm)
    }

    @Test
    fun `getX509CertificateFingerprint should return a valid SHA-256 hex string`() {
        val result = certificateManager.createNewCertificate()
        val fingerprint = certificateManager.getX509CertificateFingerprint(result.x509Certificate)
        
        // SHA-256 fingerprint format: 32 bytes separated by colons (95 characters total)
        assertNotNull(fingerprint)
        assertTrue(fingerprint.matches(Regex("^([0-9A-F]{2}:){31}[0-9A-F]{2}$")))
    }
}
