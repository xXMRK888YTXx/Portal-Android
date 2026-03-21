package com.xxmrk888ytxx.portal.data.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xxmrk888ytxx.coreandroid.fastDebugLog

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        fastDebugLog("BootReceiver")
        if (intent?.action != "android.intent.action.BOOT_COMPLETED") return
    }
}