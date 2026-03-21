package com.xxmrk888ytxx.unlockservice.wifiService

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.xxmrk888ytxx.unlockservice.R
import com.xxmrk888ytxx.unlockservice.core.ClientEntry
import com.xxmrk888ytxx.unlockservice.core.NetworkDriver
import com.xxmrk888ytxx.unlockservice.core.NotificationInfo
import com.xxmrk888ytxx.unlockservice.core.UnlockService
import com.xxmrk888ytxx.unlockservice.qualifier.WifiNetworkDriver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class WifiUnlockService @Inject constructor(
    @param:WifiNetworkDriver private val networkDriver: NetworkDriver
) : UnlockService() {

    private val connectivityManager: ConnectivityManager by lazy {
        getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }


    override val notificationInfo: NotificationInfo
        get() = NotificationInfo(111, R.string.background_service_running_wifi)


    override suspend fun waitConnectionToNetwork() {
        connectivityManager.observeLocalWifi().first { isConnected -> isConnected }
    }

    override suspend fun connect(clientId: String, clientEntry: ClientEntry) {
        networkDriver.connect(
            messagesForSendChannel = clientEntry.sendMessagesChannel,
            receivedRequestChannel = clientEntry.unlockRequests,
            clientId = clientId
        )
    }


    fun ConnectivityManager.observeLocalWifi(): Flow<Boolean> = callbackFlow {
        val activeNetwork = activeNetwork
        val caps = getNetworkCapabilities(activeNetwork)
        val isConnected = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        if (isConnected) {
            trySend(true)
            awaitClose {  }
            return@callbackFlow
        }

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }
        registerNetworkCallback(request, callback)

        awaitClose {
            unregisterNetworkCallback(callback)
        }
    }
}