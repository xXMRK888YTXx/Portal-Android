package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.data.WearDecisionPayloadValue

/**
 * Gateway for commands sent from the watch to the paired phone over Wearable Data Layer messages.
 *
 * The phone remains responsible for all unlock transports and Wake-on-LAN. The watch only sends
 * profile ids and request decisions.
 */
interface WearPhoneGateway {
    suspend fun sendUnlockCommand(clientId: String)
    suspend fun sendWakeOnLanUnlockCommand(clientId: String)
    suspend fun sendDecision(decisionId: String, decision: WearDecisionPayloadValue)
    suspend fun requestDeviceSync()
    suspend fun isPhoneAvailable(): Boolean
}
