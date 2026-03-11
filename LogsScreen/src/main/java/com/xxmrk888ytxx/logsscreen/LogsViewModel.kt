package com.xxmrk888ytxx.logsscreen

import com.xxmrk888ytxx.coreandroid.PortalViewModel
import com.xxmrk888ytxx.logsscreen.model.LogsUiEvent
import com.xxmrk888ytxx.logsscreen.model.ScreenState
import javax.inject.Inject

class LogsViewModel @Inject constructor() : PortalViewModel<ScreenState, LogsUiEvent>(ScreenState()) {
    override fun handleEvent(event: LogsUiEvent) {
        TODO("Not yet implemented")
    }
}