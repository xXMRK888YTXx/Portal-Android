package com.xxmrk888ytxx.portal.data.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.WearDecisionPayloadValue
import com.xxmrk888ytxx.portal.domain.IncomingRequestRepository
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

class WearNotificationActionReceiver @Inject constructor(
    private val wearPhoneGateway: WearPhoneGateway,
    private val incomingRequestRepository: IncomingRequestRepository,
    private val applicationScope: CoroutineScope
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return
        val decisionId = intent.getStringExtra(EXTRA_DECISION_ID) ?: return
        val decisionValue = when (intent.action) {
            ACTION_ALLOW -> WearDecisionPayloadValue.UNLOCK
            ACTION_DENY -> WearDecisionPayloadValue.CANCEL
            else -> return
        }

        fastDebugLog("Watch notification action received: action=${intent.action}, decisionId=$decisionId, decision=$decisionValue")

        NotificationManagerCompat.from(context).cancel(abs(decisionId.hashCode()))

        applicationScope.launch {
            runCatching {
                wearPhoneGateway.sendDecision(decisionId, decisionValue)
            }.onSuccess {
                fastDebugLog("Watch notification action: successfully sent decision $decisionValue for decisionId: $decisionId")
                incomingRequestRepository.clear(decisionId)
            }.onFailure {
                fastDebugLog("Watch notification action: failed to send decision $decisionValue for decisionId: $decisionId: ${it.message}")
            }
        }
    }

    companion object {
        const val ACTION_ALLOW = "com.xxmrk888ytxx.portal.wear.ACTION_ALLOW"
        const val ACTION_DENY = "com.xxmrk888ytxx.portal.wear.ACTION_DENY"
        const val EXTRA_DECISION_ID = "decisionId"
    }
}
