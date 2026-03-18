package com.xxmrk888ytxx.portal.providedContract.mainScreen

import com.xxmrk888ytxx.mainscreen.contract.ProvideSavedDevices
import com.xxmrk888ytxx.mainscreen.model.DeviceType
import com.xxmrk888ytxx.mainscreen.model.Device as MainScreenDevice
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProvideSavedDevicesImpl @Inject constructor(
    wifiDeviceRepository: WifiDeviceRepository
) : ProvideSavedDevices {
    override val devices: Flow<ImmutableList<MainScreenDevice>> = wifiDeviceRepository.devices
        .map { list -> list.map { device -> MainScreenDevice(
            deviceId = device.deviceId,
            host = device.host,
            deviceName = device.deviceName,
            deviceType = DeviceType.WIFI //TODO Change from hardcode
        ) }.toImmutableList() }
}