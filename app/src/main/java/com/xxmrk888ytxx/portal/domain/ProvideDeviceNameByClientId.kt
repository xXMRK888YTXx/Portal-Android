package com.xxmrk888ytxx.portal.domain

interface ProvideDeviceNameByClientId {
    suspend fun provideName(clientId: String): String?
}