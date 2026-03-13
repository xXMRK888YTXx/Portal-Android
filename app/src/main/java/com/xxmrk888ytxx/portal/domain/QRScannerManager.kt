package com.xxmrk888ytxx.portal.domain

interface QRScannerManager {
    suspend fun scan(): String
}