package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.data.wear.WearFinalStatusPayloadValue

interface WearPhoneGateway {
    suspend fun sendIncomingUnlockRequest(
        decisionId: String,
        clientId: String,
        deviceName: String
    )

    suspend fun sendFinalStatus(decisionId: String, status: WearFinalStatusPayloadValue)
}
