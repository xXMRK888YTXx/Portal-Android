package com.xxmrk888ytxx.portal.domain

/**
 * Validates whether a given Wearable node ID belongs to the authorized companion phone.
 */
interface WearNodeValidator {
    suspend fun isTrustedPhoneNode(nodeId: String): Boolean
}
