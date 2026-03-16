package com.xxmrk888ytxx.portal.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import com.xxmrk888ytxx.portal.domain.PermissionManager
import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.exception.BluetoothDisabledException
import com.xxmrk888ytxx.portal.exception.BluetoothNotSupportedException
import com.xxmrk888ytxx.portal.exception.BluetoothPermissionNotGrantedException
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
    override suspend fun getPairedDevices(): List<BluetoothDevice> {
        fastDebugLog("getPairedDevices")
        if (!permissionManager.isBluetoothPermissionGranted) throw BluetoothPermissionNotGrantedException()
        if (!isBluetoothEnabled) throw BluetoothDisabledException()
        return bluetoothAdapter.bondedDevices.map {
            BluetoothDevice(it.name,it.address)
        }.also { fastDebugLog("Paired devices: $it") }
    }
}