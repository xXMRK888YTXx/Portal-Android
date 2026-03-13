package com.xxmrk888ytxx.portal.providedContract.addNewDeviceScreen

import com.xxmrk888ytxx.addnewdevicescreen.contract.ScanQrCodeContract
import com.xxmrk888ytxx.addnewdevicescreen.model.ScanQrCodeResult
import com.xxmrk888ytxx.portal.domain.QRScannerManager
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.exception.QRScanCanceledException
import com.xxmrk888ytxx.portal.exception.QRScannerNotDownloadedException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ScanQrCodeContractImpl @Inject constructor(
    private val qrScannerManager: QRScannerManager,
    private val json: Json
) : ScanQrCodeContract {

    @Serializable
    private data class ScanResult(
        @SerialName("name") val deviceName: String,
        @SerialName("ip") val host: String,
        @SerialName("code") val pairCode: Int
    )

    override suspend fun requestScan(): Result<ScanQrCodeResult> = runCatching(
        Dispatchers.Default,
        onMapException = {
            when(it) {
                is QRScanCanceledException -> com.xxmrk888ytxx.addnewdevicescreen.exception.QRScanCanceledException()
                is QRScannerNotDownloadedException -> com.xxmrk888ytxx.addnewdevicescreen.exception.QRScannerNotDownloadedException()
                else -> it
            }
        }
    ) {
        val scanResult = qrScannerManager.scan()
        val parsedResult = json.decodeFromString<ScanResult>(scanResult)
        return@runCatching ScanQrCodeResult(
            deviceName = parsedResult.deviceName,
            host = parsedResult.host,
            pairCode = parsedResult.pairCode
        )
    }
}