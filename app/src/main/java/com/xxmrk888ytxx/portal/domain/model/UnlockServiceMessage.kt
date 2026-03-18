package com.xxmrk888ytxx.portal.domain.model

sealed interface UnlockServiceMessage {
    data object Unlock : UnlockServiceMessage
    data object Canceled : UnlockServiceMessage
}