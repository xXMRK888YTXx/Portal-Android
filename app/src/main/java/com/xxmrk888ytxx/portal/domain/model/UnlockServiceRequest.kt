package com.xxmrk888ytxx.portal.domain.model

sealed interface UnlockServiceRequest {
    data object Auth: UnlockServiceRequest
}