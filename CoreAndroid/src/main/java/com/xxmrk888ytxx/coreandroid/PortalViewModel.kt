package com.xxmrk888ytxx.coreandroid

import androidx.lifecycle.ViewModel
import com.xxmrk888ytxx.coreandroid.mvi.UiEvent
import com.xxmrk888ytxx.coreandroid.mvi.UiModel

abstract class PortalViewModel<STATE, EVENT : UiEvent>  : ViewModel(), UiModel<STATE, EVENT>