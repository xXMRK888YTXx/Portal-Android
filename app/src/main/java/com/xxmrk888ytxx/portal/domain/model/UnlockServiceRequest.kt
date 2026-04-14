package com.xxmrk888ytxx.portal.domain.model

sealed class UnlockServiceRequest(open val requestId: String?) {
    data class Auth(override val requestId: String?): UnlockServiceRequest(requestId)
}