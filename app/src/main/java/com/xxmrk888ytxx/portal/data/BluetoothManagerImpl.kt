package com.xxmrk888ytxx.portal.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.model.RfcommBluetoothConnection
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import com.xxmrk888ytxx.portal.domain.PermissionManager
import com.xxmrk888ytxx.portal.domain.model.BluetoothConnection
import com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice
import com.xxmrk888ytxx.portal.exception.BluetoothDisabledException
import com.xxmrk888ytxx.portal.exception.BluetoothNotSupportedException
import com.xxmrk888ytxx.portal.exception.BluetoothPermissionNotGrantedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class BluetoothManagerImpl @Inject constructor(
    private val context: Context,
    private val permissionManager: PermissionManager
) : BluetoothManager {

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
        val androidBluetoothDevice =
            bluetoothAdapter.bondedDevices.firstOrNull { it.address == macAddress }
                ?: throw IllegalArgumentException("Device $macAddress not paired")
        val socket = androidBluetoothDevice.createRfcommSocketToServiceRecord(
            UUID.fromString(PORTAL_BLUETOOTH_SERVICE_UUID)
        )
        socket.connect()
        return@withContext RfcommBluetoothConnection(socket)
    }

    private fun checkBluetoothStateAndPermission() {
        if (!permissionManager.isBluetoothPermissionGranted) throw BluetoothPermissionNotGrantedException()
        if (!isBluetoothEnabled) throw BluetoothDisabledException()
    }

    companion object {
        const val PORTAL_BLUETOOTH_SERVICE_UUID = "E0CBF06C-CD8B-4647-BB8A-263B43F0F974"
    }

}