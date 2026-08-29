package com.xxmrk888ytxx.portal.presentation.incomingRequest

/**
 * User intents from the incoming unlock request screen.
 */
sealed interface IncomingRequestEvent {
    data object NavigateBack : IncomingRequestEvent
    data object Cancel : IncomingRequestEvent
    data object Unlock : IncomingRequestEvent
}
