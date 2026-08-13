package com.xxmrk888ytxx.portal.data.service

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
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
        serviceScope.launch {
            wearCommandHandler.handleMessage(messageEvent.path, messageEvent.data)
        }
    }
}
