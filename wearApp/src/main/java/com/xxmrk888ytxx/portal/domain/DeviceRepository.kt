package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.Device
import kotlinx.coroutines.flow.StateFlow

/**
 * Local source of synced PC metadata for Wear OS UI.
 *
 * The phone is the authoritative source. The repository caches the latest device list so the watch
 * can render quickly and survive app restarts while a fresh sync request is sent.
 */
interface DeviceRepository {
    val devices: StateFlow<List<Device>>
    fun updateDevices(devices: List<Device>)
}
