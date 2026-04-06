package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.data.model.PairResponse
import com.xxmrk888ytxx.portal.data.model.WifiPairBody
import com.xxmrk888ytxx.portal.data.model.WifiUnlockBody
import com.xxmrk888ytxx.portal.domain.WifiPortalApi
import com.xxmrk888ytxx.portal.domain.model.Certificate
import com.xxmrk888ytxx.portal.domain.model.WifiPairResult
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class WifiPortalApiImpl @Inject constructor(
    private val networkFactory: NetworkFactory
) : WifiPortalApi {
    override suspend fun pair(
        host: String,
        pairCode: String,
        certificate: Certificate
    ): Result<WifiPairResult> = runCatching(Dispatchers.IO) {
        val client = networkFactory.createPairClient(certificate)
        val response = client.post("https://$host:29170/api/pair") {
            contentType(ContentType.Application.Json)
            setBody(WifiPairBody(pairCode))
        }
        fastDebugLog(response.bodyAsText())
        val body: PairResponse = response.body()
        val serverHash = response.headers[NetworkFactory.SERVER_CERTIFICATE_HASH_HEADER]
            ?: throw IllegalStateException("Server certificate hash not found")


        return@runCatching WifiPairResult(
            clientId = body.clientId,
            certificateFingerprint = serverHash,
            macAddress = body.macAddress?.replace('-',':')
        )
    }

    override suspend fun unlock(
        host: String,
        clientId: String,
        serverCertificateHash: String,
        clientCertificate: Certificate
    ): Result<Unit> = runCatching(Dispatchers.IO) {
        val client = networkFactory.createUnlockClient(clientCertificate, serverCertificateHash)
        client.post("https://$host:29170/api/unlock") {
            contentType(ContentType.Application.Json)
            setBody(WifiUnlockBody(clientId))
        }
    }

}