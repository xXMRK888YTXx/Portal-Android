package com.xxmrk888ytxx.portal.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

suspend fun MdnsManager.waitHostForClient(
    clientId: String,
    timeout: Long?
): String? {
    val timeout = timeout ?: Long.MAX_VALUE
    return withTimeoutOrNull(timeout) {
        foundedHosts.first { it.containsKey(clientId) }[clientId]?.hostIp
    }
}