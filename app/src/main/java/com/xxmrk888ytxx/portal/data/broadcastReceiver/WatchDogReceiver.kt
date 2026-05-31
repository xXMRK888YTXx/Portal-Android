package com.xxmrk888ytxx.portal.data.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xxmrk888ytxx.coreandroid.fastDebugLog

class WatchDogReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_WATCH_DOG_ALARM) return
        fastDebugLog("WatchDog alarm received")
        // Receiving this alarm starts the app process. Foreground unlock services are restored
        // from PortalApp.onCreate() according to the current app state.
    }

    companion object {
        const val ACTION_WATCH_DOG_ALARM = "com.xxmrk888ytxx.portal.ACTION_WATCH_DOG_ALARM"
    }
}
