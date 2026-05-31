package com.xxmrk888ytxx.portal.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.xxmrk888ytxx.portal.data.broadcastReceiver.WatchDogReceiver
import com.xxmrk888ytxx.portal.domain.WatchDogAlarmController
import javax.inject.Inject

class WatchDogAlarmControllerImpl @Inject constructor(
    private val context: Context
) : WatchDogAlarmController {

    private val alarmManager: AlarmManager?
        get() = context.getSystemService(AlarmManager::class.java)

    override fun scheduleRepeating(intervalMillis: Long) {
        alarmManager?.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + intervalMillis,
            intervalMillis,
            createPendingIntent()
        )
    }

    override fun cancel() {
        alarmManager?.cancel(createPendingIntent())
    }

    private fun createPendingIntent(): PendingIntent = PendingIntent.getBroadcast(
        context,
        WATCH_DOG_REQUEST_CODE,
        Intent(context, WatchDogReceiver::class.java).apply {
            action = WatchDogReceiver.ACTION_WATCH_DOG_ALARM
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private companion object {
        const val WATCH_DOG_REQUEST_CODE = 888_300
    }
}
