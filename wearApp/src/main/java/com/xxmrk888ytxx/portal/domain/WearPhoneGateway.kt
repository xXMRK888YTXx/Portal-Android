package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.data.WearDecisionPayloadValue

interface WearPhoneGateway {
    suspend fun sendUnlockCommand(clientId: String)
    suspend fun sendWakeOnLanUnlockCommand(clientId: String)
    suspend fun sendDecision(decisionId: String, decision: WearDecisionPayloadValue)
    suspend fun requestDeviceSync()
    suspend fun isPhoneAvailable(): Boolean
}
