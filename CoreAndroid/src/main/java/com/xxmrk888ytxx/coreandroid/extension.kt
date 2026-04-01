package com.xxmrk888ytxx.coreandroid

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import kotlinx.coroutines.CancellationException

inline fun Context.buildNotificationChannel(
    id: String,
    name: String,
    configuration: NotificationChannel.() -> Unit = {}
) {
    val channel =
        NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT).apply(configuration)

    val notificationManager = getSystemService<NotificationManager>()

    notificationManager?.createNotificationChannel(channel)
}

inline fun Context.buildNotification(
    channelId: String,
    configuration: Notification.Builder.() -> Unit
): Notification {
    val notificationBuilder = Notification.Builder(this, channelId)

    return notificationBuilder.apply(configuration).build()
}

inline fun saveCall(isPrintToDebug: Boolean = true, block: () -> Unit) {
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        if (isPrintToDebug)
            fastDebugLog(e)
    }
}

fun String.formatToMacAddress(): String? {
    val cleaned = this.filter { it.isDigit() || it.lowercaseChar() in 'a'..'f' }

    if (cleaned.length != 12) return null

    val sb = StringBuilder()
    for (i in cleaned.indices) {
        if (i > 0 && i % 2 == 0) {
            sb.append(":")
        }
        sb.append(cleaned[i].uppercaseChar())
    }

    return sb.toString()
}