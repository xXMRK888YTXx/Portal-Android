package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.data.model.BluetoothPairBody
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import com.xxmrk888ytxx.portal.domain.BluetoothPortalApi
import com.xxmrk888ytxx.portal.domain.model.BluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.BluetoothPairResult
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import javax.inject.Inject

class BluetoothPortalApiImpl @Inject constructor(
    private val bluetoothManager: BluetoothManager,
    private val json: Json
) : BluetoothPortalApi {
    override suspend fun pair(
        bluetoothDevice: BluetoothDevice,
        pairCode: String
    ): Result<BluetoothPairResult> = runCatching(Dispatchers.IO) {
        val pairBody = BluetoothPairBody(pairCode)
        val jsonString = json.encodeToString(pairBody)
        bluetoothManager.openConnection(bluetoothDevice)
        val bluetoothConnection = bluetoothManager.openConnection(bluetoothDevice)
        bluetoothConnection.sendData(jsonString.toByteArray())
        return@runCatching BluetoothPairResult("")
    }
}