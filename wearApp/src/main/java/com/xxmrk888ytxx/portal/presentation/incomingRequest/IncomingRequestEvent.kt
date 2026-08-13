package com.xxmrk888ytxx.portal.presentation.incomingRequest

sealed interface IncomingRequestEvent {
    data object NavigateBack : IncomingRequestEvent
    data object Cancel : IncomingRequestEvent
    data object Unlock : IncomingRequestEvent
}
