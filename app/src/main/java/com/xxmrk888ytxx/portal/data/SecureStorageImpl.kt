package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.portal.domain.SecureStorage
import com.xxmrk888ytxx.portal.domain.model.Certificate
import com.xxmrk888ytxx.portal.exception.SecureStoreException
import java.security.KeyPair
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.inject.Inject

class SecureStorageImpl @Inject constructor() : SecureStorage {

    private val keyStore by lazy {
        KeyStore.getInstance(KEY_STORE_NAME).apply { load(null) }
    }

    override suspend fun saveCertificateByAlias(
        alias: String,
        certificate: Certificate
    ) {
        keyStore
            .setKeyEntry(
                alias,
                certificate.keyPair.private,
                null,
                arrayOf(certificate.x509Certificate)
            )
    }

    override suspend fun restoreCertificateByAlias(alias: String): Certificate {
        val exception = SecureStoreException("Certificate by alias $alias not found")
        if (!keyStore.containsAlias(alias)) throw exception
        val certificate = keyStore.getCertificate(alias) as? X509Certificate ?: throw exception
        val privateKey =
            keyStore.getKey(alias, null) as? java.security.PrivateKey ?: throw exception
        val publicKey = certificate.publicKey
        val keyPair = KeyPair(publicKey, privateKey)
        return Certificate(keyPair, certificate)

    }

    private companion object {
        const val KEY_STORE_NAME = "AndroidKeyStore"
    }
}