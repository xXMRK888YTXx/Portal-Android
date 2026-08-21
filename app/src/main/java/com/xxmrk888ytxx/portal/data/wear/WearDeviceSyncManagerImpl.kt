package com.xxmrk888ytxx.portal.data.wear

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.mydictionary.DI.scope.AppScope
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.WearDeviceSyncManager
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.WifiDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AppScope
class WearDeviceSyncManagerImpl @Inject constructor(
    context: Context,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository,
    private val deviceSettingsRepository: DeviceSettingsRepository,
    private val json: Json,
    private val applicationScope: CoroutineScope
) : WearDeviceSyncManager {

    private val dataClient = Wearable.getDataClient(context)

    override fun startObserve() {
        applicationScope.launch {
            fastDebugLog("Phone: Starting Wear device sync observer")
            combine(
                wifiDeviceRepository.devices,
                bluetoothDeviceRepository.devices,
                deviceSettingsRepository.deviceSettings
            ) { wifiDevices, bluetoothDevices, _ ->
                createPayload(wifiDevices, bluetoothDevices)
            }
                .distinctUntilChanged()
                .collect {
                    fastDebugLog("Phone: Devices updated, publishing ${it.devices.size} devices to Wear DataClient")
                    publish(it)
                }
        }
    }

    override suspend fun syncNow() {
        fastDebugLog("Phone: syncNow() requested from Wear")
        publish(
            createPayload(
                wifiDeviceRepository.devices.first(),
                bluetoothDeviceRepository.devices.first()
            )
        )
    }

    private fun createPayload(
        wifiDevices: List<WifiDevice>,
        bluetoothDevices: List<BluetoothDevice>
    ): WearDevicesPayload {
        return WearDevicesPayload(
            revision = System.currentTimeMillis(),
            devices = wifiDevices.map { device ->
                WearDevicePayload(
                    clientId = device.clientId,
                    name = device.deviceName,
                    transport = WearDeviceTransportPayload.WIFI,
                    isWakeOnLanAvailable = device.wolMacAddress != null
                )
            } + bluetoothDevices.map { device ->
                WearDevicePayload(
                    clientId = device.clientId,
                    name = device.name,
                    transport = WearDeviceTransportPayload.BLUETOOTH,
                    isWakeOnLanAvailable = false
                )
            }
        )
    }

    private fun publish(payload: WearDevicesPayload) {
        val request = PutDataMapRequest.create(WearDataLayerProtocol.PROFILES_PATH).apply {
            dataMap.putLong("revision", payload.revision)
            dataMap.putString(
                WearDataLayerProtocol.PROFILE_PAYLOAD_KEY,
                json.encodeToString(payload)
            )
        }.asPutDataRequest().setUrgent()
        dataClient.putDataItem(request)
        fastDebugLog("Phone: Published device payload to Wear DataLayer with revision: ${payload.revision}")
    }
}
