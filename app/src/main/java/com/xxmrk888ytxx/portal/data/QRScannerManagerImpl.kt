package com.xxmrk888ytxx.portal.data

import android.content.Context
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.xxmrk888ytxx.coreandroid.fastDebugLog
import com.xxmrk888ytxx.portal.domain.QRScannerManager
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import com.xxmrk888ytxx.coreandroid.runCatching
import com.xxmrk888ytxx.portal.exception.FailedQRScanException
import com.xxmrk888ytxx.portal.exception.QRScanCanceledException
import com.xxmrk888ytxx.portal.exception.QRScannerNotDownloadedException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class QRScannerManagerImpl @Inject constructor(
    private val context: Context
) : QRScannerManager {
    override suspend fun scan(): String {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .enableAutoZoom()
            .build()

        val scanner = GmsBarcodeScanning.getClient(context, options)
        return suspendCancellableCoroutine { continuation ->
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    fastDebugLog("addOnSuccessListener. Barcode value: ${barcode.rawValue}")
                    val value = barcode.rawValue
                    if (value != null)
                        continuation.resume(value)
                    else continuation.resumeWithException(FailedQRScanException())
                }
                .addOnCanceledListener {
                    fastDebugLog("addOnCanceledListener")
                    continuation.resumeWithException(QRScanCanceledException())
                }
                .addOnFailureListener { e ->
                    fastDebugLog("addOnFailureListener ${e.stackTraceToString()}")
                    val exception = when (e) {
                        is MlKitException -> QRScannerNotDownloadedException()
                        else -> FailedQRScanException()
                    }
                    continuation.resumeWithException(exception)
                }
        }
    }
}