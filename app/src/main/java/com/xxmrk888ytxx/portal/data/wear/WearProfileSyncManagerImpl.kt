package com.xxmrk888ytxx.portal.data.wear

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.xxmrk888ytxx.mydictionary.DI.scope.AppScope
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.DeviceSettingsRepository
import com.xxmrk888ytxx.portal.domain.WearProfileSyncManager
import com.xxmrk888ytxx.portal.domain.WifiDeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AppScope
class WearProfileSyncManagerImpl @Inject constructor(
    context: Context,
    private val wifiDeviceRepository: WifiDeviceRepository,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository,
    private val deviceSettingsRepository: DeviceSettingsRepository,
    private val json: Json,
    private val applicationScope: CoroutineScope
) : WearProfileSyncManager {

    private val dataClient = Wearable.getDataClient(context)

    override fun startObserve() {
        applicationScope.launch {
            combine(
                wifiDeviceRepository.devices,
                bluetoothDeviceRepository.devices,
                deviceSettingsRepository.deviceSettings
            ) { wifiDevices, bluetoothDevices, _ ->
                WearProfilesPayload(
                    profiles = wifiDevices.map { device ->
                        WearProfilePayload(
                            clientId = device.clientId,
                            name = device.deviceName,
                            transport = WearTransportPayload.WIFI,
                            isWakeOnLanAvailable = device.wolMacAddress != null
                        )
                    } + bluetoothDevices.map { device ->
                        WearProfilePayload(
                            clientId = device.clientId,
                            name = device.name,
                            transport = WearTransportPayload.BLUETOOTH,
                            isWakeOnLanAvailable = false
                        )
                    }
                )
            }
                .distinctUntilChanged()
                .collect { payload ->
                    val request =
                        PutDataMapRequest.create(WearDataLayerProtocol.PROFILES_PATH).apply {
                            dataMap.putString(
                                WearDataLayerProtocol.PROFILE_PAYLOAD_KEY,
                                json.encodeToString(payload)
                            )
                        }.asPutDataRequest().setUrgent()
                    dataClient.putDataItem(request)
                }
        }
    }
}
