package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.data.model.PairBody
import com.xxmrk888ytxx.portal.data.model.PairResponse
import com.xxmrk888ytxx.portal.domain.PortalApi
import com.xxmrk888ytxx.portal.domain.model.Certificate
import com.xxmrk888ytxx.portal.domain.model.PairResult
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PortalApiImpl @Inject constructor(
    private val ktorFactory: KtorFactory
) : PortalApi {
    override suspend fun pair(
        host: String,
        pairCode: String,
        certificate: Certificate
    ): Result<PairResult> = runCatching(Dispatchers.IO) {
        val client = ktorFactory.createPairClient(certificate)
        val response = client.post("https://$host:29170/api/pair") {
            contentType(ContentType.Application.Json)
            setBody(PairBody(pairCode))
        }
        val body: PairResponse = response.body()
        val serverHash = response.headers[KtorFactory.SERVER_CERTIFICATE_HASH_HEADER] ?: throw IllegalStateException("Server certificate hash not found")


        return@runCatching PairResult(body.clientId,serverHash)
    }

}