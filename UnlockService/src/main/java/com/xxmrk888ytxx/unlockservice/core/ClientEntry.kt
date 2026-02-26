package com.xxmrk888ytxx.unlockservice.core

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow

data class ClientEntry(
    internal val connectJob: Job,
    internal val sendMessagesChannel: Channel<UnlockMessage>,
    internal val unlockRequests: MutableSharedFlow<UnlockRequest>,
)