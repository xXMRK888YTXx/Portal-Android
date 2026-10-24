package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.DeviceType

interface ProvideDeviceNameByClientId {
    suspend fun provideName(clientId: String): String?
    suspend fun provideType(clientId: String): DeviceType?
}
