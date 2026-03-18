package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import com.xxmrk888ytxx.portal.domain.MdnsManager
import com.xxmrk888ytxx.portal.domain.WifiPortalApi
import com.xxmrk888ytxx.portal.domain.model.WifiDevice
import com.xxmrk888ytxx.portal.utils.waitHostForClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeviceUnlockManagerImpl @Inject constructor(
    private val wifiPortalApi: WifiPortalApi,
    private val mdnsManager: MdnsManager,
    private val deviceSettingsRepository: DeviceSettingsRepository
) : DeviceUnlockManager {
    override suspend fun unlockWifiDevice(wifiDevice: WifiDevice): Result<Unit> =
        runCatching(Dispatchers.IO) {
            val settings = deviceSettingsRepository.getDeviceSettingsByDeviceId(wifiDevice.deviceId).first() ?: throw IllegalStateException("Device haven't settings")
            val host = when {
                settings.searchIpDynamically -> mdnsManager.waitHostForClient(wifiDevice.deviceId)
                    .also { fastDebugLog("In unlockWifiDevice mdns found host: $it") }
                    ?: wifiDevice.host.also { fastDebugLog("In unlockWifiDevice mdns not found host. Using default") }
                else -> wifiDevice.host
            }
            wifiPortalApi.unlock(
                host = host,
                clientId = wifiDevice.deviceId,
                serverCertificateHash = wifiDevice.serverCertificateFingerprint,
                clientCertificate = wifiDevice.clientCertificate
            ).getOrThrow()
        }
}