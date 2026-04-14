package com.xxmrk888ytxx.addnewdevicescreen.contract

import com.xxmrk888ytxx.addnewdevicescreen.model.ScanQrCodeResult

interface ScanQrCodeContract {
    suspend fun requestScan(): Result<ScanQrCodeResult>
}