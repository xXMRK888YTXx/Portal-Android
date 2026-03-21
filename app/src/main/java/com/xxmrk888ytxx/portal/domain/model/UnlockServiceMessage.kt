package com.xxmrk888ytxx.portal.domain.model

sealed class UnlockServiceMessage(open val requestId: String?) {
    data class Unlock(override val requestId: String?) : UnlockServiceMessage(requestId)
    data class Canceled(override val requestId: String?) : UnlockServiceMessage(requestId)
}