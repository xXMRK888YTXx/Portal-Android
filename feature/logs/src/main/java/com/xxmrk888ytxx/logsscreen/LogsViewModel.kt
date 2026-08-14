package com.xxmrk888ytxx.logsscreen

import com.xxmrk888ytxx.coreandroid.PortalViewModel
import com.xxmrk888ytxx.logsscreen.contract.ProvideLogsContract
import com.xxmrk888ytxx.logsscreen.model.LogsUiEvent
import com.xxmrk888ytxx.logsscreen.model.ScreenState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LogsViewModel @Inject constructor(
    private val providesLogsContract: ProvideLogsContract
) : PortalViewModel<ScreenState, LogsUiEvent>(ScreenState()) {

    override val state: StateFlow<ScreenState> = providesLogsContract.logs.map { logs ->
        ScreenState(logs)
    }.stateWhileSubscribed()
    override fun handleEvent(event: LogsUiEvent) {}
}