package com.xxmrk888ytxx.portal.domain

/**
 * Validates whether a given Wearable node ID belongs to an authorized Wear OS companion watch.
 */
interface WearNodeValidator {
    suspend fun isTrustedWatchNode(nodeId: String): Boolean
}
