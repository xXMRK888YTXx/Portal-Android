package com.xxmrk888ytxx.portal.data

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.ext.SdkExtensions
import androidx.annotation.RequiresExtension
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.MdnsManager
import com.xxmrk888ytxx.portal.domain.model.MdnsHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MdnsManagerImpl @Inject constructor(
    private val context: Context,

    ) : MdnsManager {

    private val wifiManager by lazy {
        context.applicationContext.getSystemService<WifiManager>()!!
    }
    private val multicastLock by lazy {
        wifiManager.createMulticastLock("mDNS_Lock").apply { setReferenceCounted(false) }
    }
    val nsdManager by lazy {
        context.applicationContext.getSystemService<NsdManager>()!!
    }

    private val mdnsScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _foundedHosts = MutableStateFlow(emptyMap<String, MdnsHost>())

    override val foundedHosts: Flow<Map<String, MdnsHost>> = _foundedHosts.asStateFlow()

    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(serviceType: String?) {
            fastDebugLog("onDiscoveryStarted $serviceType")
        }

        override fun onDiscoveryStopped(serviceType: String?) {
            fastDebugLog("onDiscoveryStopped $serviceType")
        }

        @Suppress("DEPRECATION")
        override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
            fastDebugLog("onServiceFound $serviceInfo")
            serviceInfo ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(
                    Build.VERSION_CODES.TIRAMISU
                ) >= 7
            ) {
                nsdManager.registerServiceInfoCallback(
                    serviceInfo,
                    Dispatchers.IO.asExecutor(),
                    serviceInfoCallback
                )
            } else {
                nsdManager.resolveService(serviceInfo, resolveListener)
            }
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
            fastDebugLog("onServiceLost $serviceInfo")
        }

        override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
            fastDebugLog("onStartDiscoveryFailed $serviceType $errorCode")
            stopDiscovery()
        }

        override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
            fastDebugLog("onStopDiscoveryFailed $serviceType $errorCode")
            stopDiscovery()
        }
    }

    @get:RequiresExtension(extension = Build.VERSION_CODES.TIRAMISU, version = 7)
    private val serviceInfoCallback: NsdManager.ServiceInfoCallback
        get() = object : NsdManager.ServiceInfoCallback {
            override fun onServiceInfoCallbackRegistrationFailed(errorCode: Int) {
                fastDebugLog("onServiceInfoCallbackRegistrationFailed $errorCode")
            }

            override fun onServiceInfoCallbackUnregistered() {
                fastDebugLog("onServiceInfoCallbackUnregistered")
            }

            override fun onServiceLost() {
                fastDebugLog("onServiceLost")
            }

            override fun onServiceUpdated(serviceInfo: NsdServiceInfo) {
                fastDebugLog("onServiceUpdated $serviceInfo")
                handleMdnsInfo(serviceInfo)
                nsdManager.unregisterServiceInfoCallback(this)
            }
        }

    private val resolveListener: NsdManager.ResolveListener = object : NsdManager.ResolveListener {
        override fun onResolveFailed(
            serviceInfo: NsdServiceInfo?,
            errorCode: Int
        ) {
            fastDebugLog("onResolveFailed $serviceInfo $errorCode")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
            fastDebugLog("onServiceResolved $serviceInfo")
            serviceInfo?.let { handleMdnsInfo(it) }
        }

    }

    private fun startDiscovery() {
        fastDebugLog("startDiscovery")
        multicastLock.acquire()
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    private fun stopDiscovery() {
        fastDebugLog("stopDiscovery")
        try {
            nsdManager.stopServiceDiscovery(discoveryListener)
            if (multicastLock.isHeld) {
                multicastLock.release()
            }
        }catch (_: Exception) {}
        _foundedHosts.update { emptyMap() }
    }


    @Suppress("DEPRECATION")
    private fun handleMdnsInfo(serviceInfo: NsdServiceInfo) {
        val host =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(
                    Build.VERSION_CODES.TIRAMISU
                ) >= 7
            ) {
                serviceInfo.hostAddresses.firstOrNull { it.isAnyLocalAddress }?.hostAddress
            } else {
                serviceInfo.host.hostAddress
            } ?: return
        val clients =
            serviceInfo.attributes[SUPPORTED_CLIENTS_ATTRIBUTE_KEY]?.let { String(it) }?.split(",") ?: emptyList()
        clients.forEach { clientId ->
            _foundedHosts.update { map ->
                map + (clientId to MdnsHost(clientId, host))
            }
        }

    }

    init {
        mdnsScope.launch {
            var isDiscovering = false

            _foundedHosts.subscriptionCount
                .map { count -> count > 0 } // We only care if there are subscribers (true) or not (false)
                .distinctUntilChanged()     // React only when the state changes
                .collectLatest { hasSubscribers ->
                    if (hasSubscribers) {
                        // The first subscriber appeared. Start discovery if not already running.
                        if (!isDiscovering) {
                            startDiscovery()
                            isDiscovering = true
                        }
                    } else {
                        // The last subscriber unsubscribed. Wait for 3 seconds.
                        delay(3000)

                        // If no one subscribed within 3 seconds (delay was not cancelled),
                        // stop the discovery.
                        if (isDiscovering) {
                            stopDiscovery()
                            isDiscovering = false
                        }
                    }
                }
        }
    }

    companion object {
        const val SERVICE_TYPE = "_portalwin._tcp."
        const val SUPPORTED_CLIENTS_ATTRIBUTE_KEY = "clients"
    }
}