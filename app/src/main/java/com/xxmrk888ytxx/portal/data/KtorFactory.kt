package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.portal.BuildConfig
import com.xxmrk888ytxx.portal.data.connection.WebSocketConnectionImpl
import com.xxmrk888ytxx.portal.data.trustManager.AllTrustTrustManager
import com.xxmrk888ytxx.portal.data.trustManager.TrustManagerByServerCertificateHash
import com.xxmrk888ytxx.portal.domain.connection.WebSocketConnection
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
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.net.Socket
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManager
import javax.net.ssl.X509KeyManager
import kotlin.time.Duration.Companion.seconds

class KtorFactory @Inject constructor(
    private val certificateManager: CertificateManager,
    private val jsonConverter: Json
) {

    fun createPairClient(certificate: Certificate): HttpClient = createDefaultClient {
        engine {
            config {
                val trustManager = AllTrustTrustManager()
                sslSocketFactory(
                    createMtlsSSLContext(certificate, trustManager).socketFactory,
                    trustManager
                )
                hostnameVerifier { _, _ -> true }
            }

            addNetworkInterceptor(serverHashInterrupter)
        }
    }

    fun createUnlockClient(
        certificate: Certificate,
        trustedServerHashFingerprint: String
    ): HttpClient =
        createDefaultClient {
            engine {
                mtlsConfig(certificate, trustedServerHashFingerprint)
                preconfigured = OkHttpClient.Builder()
                    .pingInterval(3, TimeUnit.SECONDS)
                    .build()

            }
        }

    //Experimental
    fun openWebSocketConnection(
        url: String,
        certificate: Certificate,
        trustedServerHashFingerprint: String
    ): WebSocketConnection {
        val client = OkHttpClient.Builder()
            .pingInterval(3, TimeUnit.SECONDS)
            .configure(certificate, trustedServerHashFingerprint)
            .build()
        return WebSocketConnectionImpl(client, url)
    }

    private fun createDefaultClient(
        block: HttpClientConfig<OkHttpConfig>.() -> Unit = {}
    ): HttpClient {
        return HttpClient(OkHttp) {
            install(Logging) {
                level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.INFO
            }

            install(ContentNegotiation) {
                json(jsonConverter)
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
            }

            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(jsonConverter)
                pingInterval = 3.seconds
            }
            block()
        }
    }

    private fun OkHttpConfig.mtlsConfig(
        certificate: Certificate,
        trustedServerHashFingerprint: String
    ) {
        config {
            configure(certificate, trustedServerHashFingerprint)
        }
    }

    private fun createMtlsSSLContext(
        certificate: Certificate,
        trustManager: TrustManager
    ): SSLContext {
        val keyManager = object : X509KeyManager {
            private val alias = "PrivateKeyAlias"

            override fun getClientAliases(
                keyType: String?,
                issuers: Array<out Principal>?
            ): Array<String> = arrayOf(alias)

            override fun chooseClientAlias(
                keyType: Array<out String>?,
                issuers: Array<out Principal>?,
                socket: Socket?
            ): String = alias

            override fun getServerAliases(
                keyType: String?,
                issuers: Array<out Principal>?
            ): Array<String> = arrayOf(alias)

            override fun chooseServerAlias(
                keyType: String?,
                issuers: Array<out Principal>?,
                socket: Socket?
            ): String = alias

            override fun getCertificateChain(requestedAlias: String?): Array<X509Certificate>? =
                if (requestedAlias == alias) arrayOf(certificate.x509Certificate) else null

            override fun getPrivateKey(requestedAlias: String?): PrivateKey? =
                if (requestedAlias == alias) certificate.keyPair.private else null
        }
        val context = SSLContext.getInstance("TLS")
        context.init(arrayOf(keyManager), arrayOf(trustManager), null)
        return context
    }

    private val serverHashInterrupter by lazy {
        Interceptor { chain ->
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
                .header(SERVER_CERTIFICATE_HASH_HEADER, hash)
                .build()
        }
    }


    fun OkHttpClient.Builder.configure(
        certificate: Certificate,
        trustedServerHashFingerprint: String
    ): OkHttpClient.Builder {
        val trustManager = TrustManagerByServerCertificateHash(
            certificateManager = certificateManager,
            expectedServerHash = trustedServerHashFingerprint
        )
        sslSocketFactory(
            createMtlsSSLContext(certificate, trustManager).socketFactory,
            trustManager
        )
        hostnameVerifier { _, _ -> true }
        return this
    }


    companion object {
        const val SERVER_CERTIFICATE_HASH_HEADER = "X-Server-Certificate-Hash"
    }
}