package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.MdnsHost
import kotlinx.coroutines.flow.Flow

interface MdnsManager {
    val foundedHosts: Flow<Map<String, MdnsHost>>
}