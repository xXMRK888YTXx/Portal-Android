package com.xxmrk888ytxx.logsscreen.contract

import kotlinx.coroutines.flow.Flow

interface ProvideLogsContract {
    val logs: Flow<List<Pair<Long,String>>>
}