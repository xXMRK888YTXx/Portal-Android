package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.portal.BuildConfig
import com.xxmrk888ytxx.portal.data.trustManager.AllTrustTrustManager
import com.xxmrk888ytxx.portal.domain.CertificateManager
import com.xxmrk888ytxx.portal.domain.model.Certificate
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import java.net.Socket
import java.security.KeyStore
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.UUID
import javax.inject.Inject
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManager
import javax.net.ssl.X509KeyManager

class KtorFactory @Inject constructor(
    private val certificateManager: CertificateManager
) {

    fun createDefaultClient(
        block: HttpClientConfig<OkHttpConfig>.() -> Unit = {}
    ): HttpClient {
        return HttpClient(OkHttp) {
            install(Logging) {
                level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.INFO
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
            }
            block()
        }
    }

    fun createPairClient(certificate: Certificate): HttpClient = createDefaultClient {
        engine {
            config {
                val trustManager = AllTrustTrustManager()
                sslSocketFactory(
                    createMtlsContext(certificate, trustManager).socketFactory,
                    trustManager
                )
                hostnameVerifier { _, _ -> true }
            }

            addNetworkInterceptor(Interceptor { chain ->
                val connection = chain.connection()
                    ?: throw IllegalStateException("No connection")

                val sslSocket = connection.socket() as? SSLSocket
                    ?: throw IllegalStateException("Not an SSL connection")

                val session = sslSocket.session
                val rawCerts = try {
                    session.peerCertificates
                } catch (e: SSLPeerUnverifiedException) {
                    throw IllegalStateException("Certificate not available yet: ${e.message}")
                }

                val serverCert = rawCerts.firstOrNull() as? X509Certificate
                    ?: throw IllegalStateException("No certificates found")

                val hash = certificateManager.getX509CertificateFingerprint(serverCert)

                val originalResponse = chain.proceed(chain.request())

                return@Interceptor originalResponse.newBuilder()
                    .header(KtorFactory.SERVER_CERTIFICATE_HASH_HEADER, hash) // <--- Добавляем в ОТВЕТ
                    .build()
            })
        }
    }

    fun createMtlsContext(certificate: Certificate, trustManager: TrustManager): SSLContext {
        val keyManager = object : X509KeyManager {
            private val alias = "PrivateKeyAlias"

            override fun getClientAliases(keyType: String?, issuers: Array<out Principal>?): Array<String> = arrayOf(alias)

            override fun chooseClientAlias(keyType: Array<out String>?, issuers: Array<out Principal>?, socket: Socket?): String = alias

            override fun getServerAliases(keyType: String?, issuers: Array<out Principal>?): Array<String> = arrayOf(alias)

            override fun chooseServerAlias(keyType: String?, issuers: Array<out Principal>?, socket: Socket?): String = alias

            override fun getCertificateChain(requestedAlias: String?): Array<X509Certificate>? = if (requestedAlias == alias) arrayOf(certificate.x509Certificate) else null

            override fun getPrivateKey(requestedAlias: String?): PrivateKey? = if (requestedAlias == alias) certificate.keyPair.private else null
        }
        val context = SSLContext.getInstance("TLS")
        context.init(arrayOf(keyManager), arrayOf(trustManager), null)
        return context
    }

    companion object {
        const val SERVER_CERTIFICATE_HASH_HEADER = "X-Server-Certificate-Hash"
    }
}