package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.domain.DeviceUnlockManager
import com.xxmrk888ytxx.portal.domain.PortalApi
import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import javax.inject.Inject

class DeviceUnlockManagerImpl @Inject constructor(
    private val portalApi: PortalApi
) : DeviceUnlockManager {
    override suspend fun unlockWifiDevice(device: Device): Result<Unit> =
        runCatching(Dispatchers.IO) {
            portalApi.unlock(
                host = device.host,
                clientId = device.deviceId,
                serverCertificateHash = device.serverCertificateFingerprint,
                clientCertificate = device.clientCertificate
            ).getOrThrow()
        }
}