package com.xxmrk888ytxx.portal.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.saveCall
import com.xxmrk888ytxx.portal.domain.SecureStorage
import com.xxmrk888ytxx.portal.domain.model.Certificate
import com.xxmrk888ytxx.portal.exception.SecureStoreException
import java.security.KeyPair
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
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

    private suspend fun createKeyForObserveBiometricEnvironment() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEY_STORE_NAME
        )

        val builder = KeyGenParameterSpec.Builder(
            KEY_ALIAS_NAME_FOR_OBSERVE_BIOMETRIC_ENVIRONMENT,
            KeyProperties.PURPOSE_ENCRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)

        keyGenerator.init(builder.build())
        keyGenerator.generateKey()
    }

    override suspend fun isHaveChangesInBiometricEnvironment(): Boolean {
        if (!keyStore.containsAlias(KEY_ALIAS_NAME_FOR_OBSERVE_BIOMETRIC_ENVIRONMENT)) {
            createKeyForObserveBiometricEnvironment()
            return false
        }

        return try {
            val key = keyStore.getKey(KEY_ALIAS_NAME_FOR_OBSERVE_BIOMETRIC_ENVIRONMENT, null)
            val cipher = Cipher.getInstance(AES_ALGORITHM)

            cipher.init(Cipher.ENCRYPT_MODE, key)
            fastDebugLog("Changes in biometric environment not detected")
            false
        } catch (e: KeyPermanentlyInvalidatedException) {
            fastDebugLog("Changes in biometric environment detected: $e")
            resetKeyForObserveEnvironment()
            createKeyForObserveBiometricEnvironment()
            true
        } catch (e: Exception) {
            fastDebugLog("System error during key check: $e")
            false
        }
    }

    override suspend fun resetKeyForObserveEnvironment() = saveCall {
        keyStore.deleteEntry(KEY_ALIAS_NAME_FOR_OBSERVE_BIOMETRIC_ENVIRONMENT)
    }

    private companion object {
        const val KEY_ALIAS_NAME_FOR_OBSERVE_BIOMETRIC_ENVIRONMENT = "observe_biometric_environment"
        const val AES_ALGORITHM = "AES/GCM/NoPadding"
        const val KEY_STORE_NAME = "AndroidKeyStore"
    }
}