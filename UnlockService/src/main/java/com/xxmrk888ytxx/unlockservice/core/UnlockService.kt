package com.xxmrk888ytxx.unlockservice.core

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.unlockservice.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

abstract class UnlockService : Service(), UnlockServiceController {

    internal abstract val notificationInfo: NotificationInfo
    protected val serviceScope = CoroutineScope(Dispatchers.IO)

    protected val _unlockRequests = MutableSharedFlow<UnlockRequest>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val unlockRequests: Flow<UnlockRequest> = _unlockRequests.asSharedFlow()

    protected val sendMessagesChannel = Channel<UnlockMessage>(Channel.BUFFERED)

    override fun onCreate() {
        super.onCreate()
        buildNotificationChannel(
            id = FOREGROUND_CHANNEL_ID,
            name = getString(R.string.unlock_background_service)
        )
        val notification = buildNotification(FOREGROUND_CHANNEL_ID) {
            setContentTitle(getString(R.string.unlock_background_service))
            setContentText(getString(notificationInfo.textResId))
        }
        startForeground(notificationInfo.id, notification)
        serviceScope.launch {
            payload()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        sendMessagesChannel.close()
    }

    override fun onBind(intent: Intent?): IBinder? = UnlockBinder()

    @OptIn(DelicateCoroutinesApi::class)
    override fun sendMessage(message: UnlockMessage) {
        if (sendMessagesChannel.isClosedForSend) return
        sendMessagesChannel.trySend(message)
    }

    abstract suspend fun waitConnection()
    abstract suspend fun connect()

    protected open suspend fun payload() {
        var retryDelay = 1_000L
        val maxDelay = 60_000L

        while (currentCoroutineContext().isActive) {
            try {
                waitConnection()
                connect()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                fastDebugLog("Exception in payload: $e")
            }

            delay(retryDelay)


            retryDelay = (retryDelay * 2).coerceAtMost(maxDelay)
        }
    }

    inner class UnlockBinder : Binder() {
        val controller: UnlockServiceController = this@UnlockService
    }

    companion object {
        const val FOREGROUND_CHANNEL_ID = "foreground_notification"
    }
}