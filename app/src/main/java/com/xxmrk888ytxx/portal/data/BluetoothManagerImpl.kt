package com.xxmrk888ytxx.portal.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.connection.RfcommBluetoothConnection
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import com.xxmrk888ytxx.portal.domain.PermissionManager
import com.xxmrk888ytxx.portal.domain.connection.BluetoothConnection
import com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice
import com.xxmrk888ytxx.portal.exception.BluetoothDisabledException
import com.xxmrk888ytxx.portal.exception.BluetoothNotSupportedException
import com.xxmrk888ytxx.portal.exception.BluetoothPermissionNotGrantedException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

private typealias MacAddress = String

class BluetoothManagerImpl @Inject constructor(
    private val context: Context,
    private val permissionManager: PermissionManager
) : BluetoothManager {

    private val cashedConnectionMap: MutableStateFlow<Map<MacAddress, BluetoothConnection>> = MutableStateFlow(emptyMap())
    private val observeCloseCashedConnectionScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val cashedConnectionOperationMutex = Mutex()

    val bluetoothManager: android.bluetooth.BluetoothManager by lazy {
        context.getSystemService<android.bluetooth.BluetoothManager>()
            ?: throw BluetoothNotSupportedException()
    }
    val bluetoothAdapter: BluetoothAdapter by lazy {
        bluetoothManager.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter.isEnabled

    @SuppressLint("MissingPermission")
    override suspend fun getPairedDevices(): List<PairedBluetoothDevice> {
        fastDebugLog("getPairedDevices")
        checkBluetoothStateAndPermission()
        return bluetoothAdapter.bondedDevices.map {
            PairedBluetoothDevice(it.name, it.address)
        }.also { fastDebugLog("Paired devices: $it") }
    }

    override suspend fun openConnection(macAddress: String): BluetoothConnection = withContext(Dispatchers.IO) {
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

    private suspend fun getCashedConnection(macAddress: MacAddress): BluetoothConnection? = cashedConnectionOperationMutex.withLock {
        return cashedConnectionMap.value[macAddress]
    }

    private suspend fun addCashedConnection(
        macAddress: MacAddress,
        connection: BluetoothConnection
    ) = cashedConnectionOperationMutex.withLock {
        cashedConnectionMap.update { it.toMutableMap().apply { put(macAddress, connection)  } }
        observeCloseCashedConnectionScope.launch {
            connection.isClosed.first { isClosed -> isClosed }
            cashedConnectionOperationMutex.withLock {
                cashedConnectionMap.update { it.toMutableMap().apply { remove(macAddress) } }
            }
        }
    }

    private fun checkBluetoothStateAndPermission() {
        if (!permissionManager.isBluetoothPermissionGranted) throw BluetoothPermissionNotGrantedException()
        if (!isBluetoothEnabled) throw BluetoothDisabledException()
    }

    companion object {
        const val PORTAL_BLUETOOTH_SERVICE_UUID = "E0CBF06C-CD8B-4647-BB8A-263B43F0F974"
    }

}