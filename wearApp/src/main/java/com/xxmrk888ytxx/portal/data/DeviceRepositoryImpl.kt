package com.xxmrk888ytxx.portal.data

import androidx.datastore.preferences.core.stringPreferencesKey
import com.xxmrk888ytxx.portal.domain.DeviceRepository
import com.xxmrk888ytxx.portal.domain.model.Device
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * DataStore-backed implementation of [DeviceRepository].
 *
 * It stores only the serialized metadata snapshot received from the phone. Updates are immediately
 * reflected in memory and then persisted asynchronously.
 */
class DeviceRepositoryImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage,
    private val json: Json,
    private val applicationScope: CoroutineScope
) : DeviceRepository {

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    override val devices: StateFlow<List<Device>> = _devices.asStateFlow()

    init {
        applicationScope.launch {
            preferencesStorage.getProperty(KEY_DEVICES, "[]").collect { rawDevices ->
                _devices.value = runCatching {
                    json.decodeFromString<List<Device>>(rawDevices)
                }.getOrDefault(emptyList())
            }
        }
    }

    override fun updateDevices(devices: List<Device>) {
        _devices.value = devices
        applicationScope.launch {
            preferencesStorage.writeProperty(KEY_DEVICES, json.encodeToString(devices))
        }
    }

    private companion object {
        val KEY_DEVICES = stringPreferencesKey("devices")
    }
}
