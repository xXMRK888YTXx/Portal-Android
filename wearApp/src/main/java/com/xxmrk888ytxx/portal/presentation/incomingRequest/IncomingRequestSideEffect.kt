package com.xxmrk888ytxx.portal.presentation.incomingRequest

sealed interface IncomingRequestSideEffect {
    data object NavigateBack : IncomingRequestSideEffect
    data object ShowDecisionError : IncomingRequestSideEffect
}
