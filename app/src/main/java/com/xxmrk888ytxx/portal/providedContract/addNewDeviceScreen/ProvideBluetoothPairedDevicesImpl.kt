package com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen

import com.xxmrk888ytxx.addnewdevicescreen.contract.ProvideBluetoothPairedDevices
import com.xxmrk888ytxx.addnewdevicescreen.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.exception.BluetoothDisabledException
import com.xxmrk888ytxx.portal.exception.BluetoothNotSupportedException
import com.xxmrk888ytxx.portal.exception.BluetoothPermissionNotGrantedException

class ProvideBluetoothPairedDevicesImpl @Inject constructor(
    private val bluetoothManager: BluetoothManager
) : ProvideBluetoothPairedDevices {
    override suspend fun getPairedDevices(): Result<List<BluetoothDevice>> =
        runCatching(Dispatchers.IO, onMapException = {
            when(it) {
                is BluetoothDisabledException -> com.xxmrk888ytxx.addnewdevicescreen.exception.BluetoothDisabledException()
                is BluetoothNotSupportedException -> com.xxmrk888ytxx.addnewdevicescreen.exception.BluetoothNotSupportedException()
                is BluetoothPermissionNotGrantedException -> com.xxmrk888ytxx.addnewdevicescreen.exception.BluetoothPermissionNotGrantedException()
                else -> it
            }
        }) {
            bluetoothManager
                .getPairedDevices()
                .map {
                    BluetoothDevice(it.name, it.macAddress)
                }
        }
}