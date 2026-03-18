package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.data.model.BluetoothPairBody
import com.xxmrk888ytxx.portal.data.model.PairResponse
import com.xxmrk888ytxx.portal.domain.BluetoothManager
import com.xxmrk888ytxx.portal.domain.BluetoothPortalApi
import com.xxmrk888ytxx.portal.domain.model.PairedBluetoothDevice
import com.xxmrk888ytxx.portal.domain.model.BluetoothPairResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class BluetoothPortalApiImpl @Inject constructor(
    private val bluetoothManager: BluetoothManager,
    private val json: Json
) : BluetoothPortalApi {
    override suspend fun pair(
        pairedBluetoothDevice: PairedBluetoothDevice,
        pairCode: String
    ): BluetoothPairResult = withContext(Dispatchers.IO) {
        val pairBody = BluetoothPairBody(pairCode)
        val jsonString = json.encodeToString(pairBody)
        val bluetoothConnection = bluetoothManager.openConnection(pairedBluetoothDevice)
        bluetoothConnection.sendData(jsonString.toByteArray())
        fastDebugLog("bluetoothConnection.sendData")
        val pairResponse: PairResponse = bluetoothConnection.incomingData
            .mapNotNull { data ->
                try {
                    json.decodeFromString<PairResponse>(data.toString(Charsets.UTF_8))
                } catch (e: Exception) {
                    fastDebugLog(e)
                    null
                }
            }
            .first()
        fastDebugLog("$pairResponse PAIRED")
        return@withContext BluetoothPairResult(pairResponse.clientId)
    }
}