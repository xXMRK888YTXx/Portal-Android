package com.xxmrk888ytxx.portal.domain

interface WOLManager {
    suspend fun sendWOLRequest(macAddress: String): Result<Unit>
}