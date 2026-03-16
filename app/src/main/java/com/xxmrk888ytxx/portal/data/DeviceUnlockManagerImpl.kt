package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import com.xxmrk888ytxx.portal.domain.MdnsManager
import com.xxmrk888ytxx.portal.domain.WifiPortalApi
import com.xxmrk888ytxx.portal.domain.model.Device
import com.xxmrk888ytxx.portal.utils.waitHostForClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeviceUnlockManagerImpl @Inject constructor(
    private val wifiPortalApi: WifiPortalApi,
    private val mdnsManager: MdnsManager,
    private val deviceSettingsRepository: DeviceSettingsRepository
) : DeviceUnlockManager {
    override suspend fun unlockWifiDevice(device: Device): Result<Unit> =
        runCatching(Dispatchers.IO) {
            val settings = deviceSettingsRepository.getDeviceSettingsByDeviceId(device.deviceId).first() ?: throw IllegalStateException("Device haven't settings")
            val host = when {
                settings.searchIpDynamically -> mdnsManager.waitHostForClient(device.deviceId)
                    .also { fastDebugLog("In unlockWifiDevice mdns found host: $it") }
                    ?: device.host.also { fastDebugLog("In unlockWifiDevice mdns not found host. Using default") }
                else -> device.host
            }
            wifiPortalApi.unlock(
                host = host,
                clientId = device.deviceId,
                serverCertificateHash = device.serverCertificateFingerprint,
                clientCertificate = device.clientCertificate
            ).getOrThrow()
        }
}