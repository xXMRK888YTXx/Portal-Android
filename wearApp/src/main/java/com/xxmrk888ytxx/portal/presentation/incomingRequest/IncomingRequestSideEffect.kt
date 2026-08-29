package com.xxmrk888ytxx.portal.presentation.incomingRequest

/**
 * One-off effects emitted after handling an incoming unlock decision.
 */
sealed interface IncomingRequestSideEffect {
    data object NavigateBack : IncomingRequestSideEffect
    data object ShowDecisionError : IncomingRequestSideEffect
}
