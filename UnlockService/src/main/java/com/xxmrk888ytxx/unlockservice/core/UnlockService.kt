package com.xxmrk888ytxx.unlockservice.core

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.unlockservice.R
import com.xxmrk888ytxx.unlockservice.exception.InvalidClientIdException
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

abstract class UnlockService : Service(), UnlockServiceController {

    internal abstract val notificationInfo: NotificationInfo
    protected val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    protected val clientEntries = MutableStateFlow(mapOf<String, ClientEntry>())

    private var idleModDetectedCallback: IdleModDetectedCallback? = null

    private val stopServiceIfIdleJob = serviceScope.launch {
        while (isActive) {
            fastDebugLog("Waiting for idle mod")
            clientEntries.first { it.isEmpty() }
            fastDebugLog("Detected idle mod. 5 second before stop the service")
            delay(5000)
            if (clientEntries.value.isEmpty()) {
                fastDebugLog("Try stop the service because no clients")
                if (idleModDetectedCallback?.isCanStopService() == true)
                    stopSelf().also { fastDebugLog("Service stopped") }
            } else {
                fastDebugLog("Idle mod canceled.")
            }
        }
    }

    override fun getUnlockRequestsForHost(clientId: String): Flow<UnlockRequest>? =
        clientEntries.value[clientId]?.unlockRequests?.receiveAsFlow()

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
        fastDebugLog("Service: $this onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        fastDebugLog("Service: $this onDestroy")

    }

    override fun onBind(intent: Intent?): IBinder? = UnlockBinder()

    @OptIn(DelicateCoroutinesApi::class)
    override fun sendMessage(clientId: String, message: UnlockMessage) {
        val channel = clientEntries.value[clientId]?.sendMessagesChannel ?: return
        if (channel.isClosedForSend) return
        channel.trySend(message)
    }

    override fun startListeningUnlockRequest(clientId: String): Flow<UnlockRequest> {
        fastDebugLog("Service: $this startListeningUnlockRequest for $clientId")
        val job = getPayloadJob(clientId)
        val pair = clientId to ClientEntry(
            connectJob = job,
            sendMessagesChannel = Channel(Channel.BUFFERED),
            unlockRequests = Channel(capacity = Channel.BUFFERED)
        )
        clientEntries.update {
            it + pair
        }
        job.start()
        return getUnlockRequestsForHost(clientId)!!
    }

    override fun stopListeningUnlockRequest(clientId: String) {
        fastDebugLog("Service: $this stopListeningUnlockRequest for $clientId")
        val clientEntry = clientEntries.value[clientId] ?: return
        clientEntries.update { it.toMutableMap().apply { remove(clientId) } }
        clientEntry.connectJob.cancel()
        clientEntry.unlockRequests.close()
        clientEntry.sendMessagesChannel.close()
    }

    abstract suspend fun waitConnection()
    abstract suspend fun connect(clientId: String, clientEntry: ClientEntry)

    protected open suspend fun payload(clientId: String) {
        var retryDelay = 1_000L
        val maxDelay = 10_000L

        while (currentCoroutineContext().isActive) {
            try {
                fastDebugLog("Service: $this waitConnection")
                waitConnection()
                val entry = clientEntries.value[clientId] ?: return
                fastDebugLog("Service: $this connect")
                connect(clientId, entry)
            } catch (e: CancellationException) {
                fastDebugLog("CancellationException")
                throw e
            } catch (e: InvalidClientIdException) {
                fastDebugLog(e)
                return
            } catch (e: Exception) {
                fastDebugLog("Exception in payload: $e")
            }

            delay(retryDelay)


            retryDelay = (retryDelay + 1000L).coerceAtMost(maxDelay)
        }
    }


    override fun setIdleModCallback(callback: IdleModDetectedCallback) {
        idleModDetectedCallback = callback
    }

    inner class UnlockBinder : Binder() {
        val controller: UnlockServiceController = this@UnlockService
    }

    private fun getPayloadJob(host: String): Job =
        serviceScope.launch(Dispatchers.IO, start = CoroutineStart.LAZY) { payload(host) }
            .also { it.invokeOnCompletion { stopListeningUnlockRequest(host) } }

    companion object {
        const val FOREGROUND_CHANNEL_ID = "foreground_notification"
    }
}