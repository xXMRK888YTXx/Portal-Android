package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.UnlockServiceRequest

interface UnlockRequestHandler {
    suspend fun onNewRequest(clientId: String,request: UnlockServiceRequest)
}