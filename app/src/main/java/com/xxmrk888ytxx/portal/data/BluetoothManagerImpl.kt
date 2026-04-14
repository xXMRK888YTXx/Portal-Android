package com.xxmrk888ytxx.portal.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.connection.RfcommBluetoothConnection
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import com.xxmrk888ytxx.portal.domain.MacAddress
import com.xxmrk888ytxx.portal.domain.PermissionManager
import com.xxmrk888ytxx.portal.domain.connection.BluetoothConnection
import com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice
import com.xxmrk888ytxx.portal.exception.BluetoothDisabledException
import com.xxmrk888ytxx.portal.exception.BluetoothNotSupportedException
import com.xxmrk888ytxx.portal.exception.BluetoothPermissionNotGrantedException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class BluetoothManagerImpl @Inject constructor(
    private val context: Context,
    private val permissionManager: PermissionManager
) : BluetoothManager {

    private val cashedConnectionMap: MutableStateFlow<Map<MacAddress, BluetoothConnection>> = MutableStateFlow(emptyMap())
    private val observeCloseCashedConnectionScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val openConnectionMutex = Mutex()

    val bluetoothManager: android.bluetooth.BluetoothManager by lazy {
        context.getSystemService<android.bluetooth.BluetoothManager>()
            ?: throw BluetoothNotSupportedException()
    }
    val bluetoothAdapter: BluetoothAdapter by lazy {
        bluetoothManager.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter.isEnabled

    private val _pairedDevices = MutableStateFlow<Set<MacAddress>?>(null)

    override val pairedDeviceMacAddresses: Flow<Set<MacAddress>?> = _pairedDevices.asStateFlow()

    private val updatePairedDeviceMutex = Mutex()

    @SuppressLint("MissingPermission")
    override suspend fun getPairedDevices(): List<PairedBluetoothDevice> {
        fastDebugLog("getPairedDevices")
        checkBluetoothStateAndPermission()
        return bluetoothAdapter.bondedDevices.map {
            PairedBluetoothDevice(it.name, it.address)
        }.also { fastDebugLog("Paired devices: $it") }
    }

    override suspend fun openConnection(macAddress: String): BluetoothConnection = withContext(Dispatchers.IO) {
        openConnectionMutex.withLock {
            checkBluetoothStateAndPermission()
            val cashedConnection = getCashedConnection(macAddress)
            if (cashedConnection != null) {
                cashedConnection.acquire()
                return@withContext cashedConnection
            }
            val androidBluetoothDevice =
                bluetoothAdapter.bondedDevices.firstOrNull { it.address == macAddress }
                    ?: throw IllegalArgumentException("Device $macAddress not paired")
            val socket = androidBluetoothDevice.createRfcommSocketToServiceRecord(
                UUID.fromString(PORTAL_BLUETOOTH_SERVICE_UUID)
            )
            socket.connect()
            return@withContext RfcommBluetoothConnection(socket).also { addCashedConnection(macAddress, it) }
        }
    }

    override suspend fun updatePairedDeviceMacAddresses() = withContext(Dispatchers.IO) {
        updatePairedDeviceMutex.withLock {
            try {
                checkBluetoothStateAndPermission()
                _pairedDevices.value = bluetoothAdapter.bondedDevices.map { it.address }.toSet()
            }catch (e: Exception) {
                fastDebugLog("Failed to update paired devices. Error: $e")
                _pairedDevices.value = null
            }
        }
    }

    private suspend fun getCashedConnection(macAddress: MacAddress): BluetoothConnection? {
        return cashedConnectionMap.value[macAddress]
    }

    private suspend fun addCashedConnection(
        macAddress: MacAddress,
        connection: BluetoothConnection
    ) {
        cashedConnectionMap.update { it.toMutableMap().apply { put(macAddress, connection)  } }
        observeCloseCashedConnectionScope.launch {
            connection.isClosed.first { isClosed -> isClosed }
            openConnectionMutex.withLock { cashedConnectionMap.update { it.toMutableMap().apply { remove(macAddress) } } }
        }
    }

    private fun checkBluetoothStateAndPermission() {
        if (!permissionManager.isNearbyDevicesPermissionGranted) throw BluetoothPermissionNotGrantedException()
        if (!isBluetoothEnabled) throw BluetoothDisabledException()
    }

    companion object {
        const val PORTAL_BLUETOOTH_SERVICE_UUID = "E0CBF06C-CD8B-4647-BB8A-263B43F0F974"
    }

}