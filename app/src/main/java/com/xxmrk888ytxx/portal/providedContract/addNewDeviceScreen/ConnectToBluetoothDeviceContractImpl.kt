package com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen

import com.xxmrk888ytxx.addnewdevicescreen.contract.ConnectToBluetoothDeviceContract
import com.xxmrk888ytxx.addnewdevicescreen.model.BluetoothDevice
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.domain.BluetoothDeviceRepository
import com.xxmrk888ytxx.portal.domain.BluetoothPortalApi
import com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ConnectToBluetoothDeviceContractImpl @Inject constructor(
    private val bluetoothPortalApi: BluetoothPortalApi,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository
) : ConnectToBluetoothDeviceContract {
    override suspend fun connect(
        deviceName: String,
        pairCode: String,
        bluetoothDevice: BluetoothDevice
    ): Result<String> = runCatching(Dispatchers.IO) {
        val pairResult = bluetoothPortalApi.pair(bluetoothDevice.toDomainModel(), pairCode)
        bluetoothDeviceRepository.saveDevice(
            com.xxmrk888ytxx.portal.domain.model.BluetoothDevice(
                pairResult.clientId,
                deviceName,
                bluetoothDevice.macAddress
            )
        )
        return@runCatching pairResult.clientId
    }

    private fun BluetoothDevice.toDomainModel(): PairedBluetoothDevice =
        PairedBluetoothDevice(name, macAddress)
}