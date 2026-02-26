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
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

abstract class UnlockService : Service(), UnlockServiceController {

    internal abstract val notificationInfo: NotificationInfo
    protected val serviceScope = CoroutineScope(Dispatchers.IO)

    protected val clientEntries = mutableMapOf<String, ClientEntry>()


    override fun getUnlockRequestsForHost(clientId: String): Flow<UnlockRequest>? =
        clientEntries[clientId]?.unlockRequests?.receiveAsFlow()

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
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = UnlockBinder()

    @OptIn(DelicateCoroutinesApi::class)
    override fun sendMessage(clientId: String, message: UnlockMessage) {
        val channel = clientEntries[clientId]?.sendMessagesChannel ?: return
        if (channel.isClosedForSend) return
        channel.trySend(message)
    }

    override fun startListeningUnlockRequest(clientId: String): Flow<UnlockRequest> {
        val job = getPayloadJob(clientId)
        clientEntries[clientId] = ClientEntry(
            connectJob = job,
            sendMessagesChannel = Channel(Channel.BUFFERED),
            unlockRequests = Channel(capacity = Channel.BUFFERED)
        )
        job.start()
        return getUnlockRequestsForHost(clientId)!!
    }

    override fun stopListening(clientId: String) {
        val clientEntry = clientEntries.remove(clientId) ?: return
        clientEntry.connectJob.cancel()
        clientEntry.unlockRequests.close()
        clientEntry.sendMessagesChannel.close()
    }

    abstract suspend fun waitConnection()
    abstract suspend fun connect(clientId: String, clientEntry: ClientEntry)

    protected open suspend fun payload(clientId: String) {
        var retryDelay = 1_000L
        val maxDelay = 60_000L

        while (currentCoroutineContext().isActive) {
            try {
                waitConnection()
                val entry = clientEntries[clientId] ?: return
                connect(clientId,entry)
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

    private fun getPayloadJob(host: String): Job =
        serviceScope.launch(Dispatchers.IO, start = CoroutineStart.LAZY) { payload(host) }.also { it.invokeOnCompletion { stopListening(host) } }

    companion object {
        const val FOREGROUND_CHANNEL_ID = "foreground_notification"
    }
}