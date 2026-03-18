package com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen

import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToBluetoothDeviceContract
import com.xxmrk888ytxx.addnewdevicescreen.model.BluetoothDevice
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.domain.BluetoothPortalApi
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ConnectToBluetoothDeviceContractImpl @Inject constructor(
    private val bluetoothPortalApi: BluetoothPortalApi
) : ConnectToBluetoothDeviceContract {
    override suspend fun connect(
        deviceName: String,
        pairCode: String,
        bluetoothDevice: BluetoothDevice
    ): Result<String> = runCatching(Dispatchers.IO) {
        val pairResult = bluetoothPortalApi.pair(bluetoothDevice.toDomainModel(), pairCode)
        return@runCatching "xyi"
    }

    private fun BluetoothDevice.toDomainModel(): com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice =
        com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice(name, macAddress)
}