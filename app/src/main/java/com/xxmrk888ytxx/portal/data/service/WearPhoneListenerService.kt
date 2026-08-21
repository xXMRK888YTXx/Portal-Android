package com.xxmrk888ytxx.portal.data.service

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.wear.WearCommandHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class WearPhoneListenerService @Inject constructor(
    private val wearCommandHandler: WearCommandHandler
) : WearableListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        fastDebugLog("Phone: WearPhoneListenerService received message from ${messageEvent.sourceNodeId}, path: ${messageEvent.path} (${messageEvent.data.size} bytes)")
        serviceScope.launch {
            wearCommandHandler.handleMessage(
                sourceNodeId = messageEvent.sourceNodeId,
                path = messageEvent.path,
                data = messageEvent.data
            )
        }
    }
}
