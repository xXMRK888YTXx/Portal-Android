package com.xxmrk888ytxx.deviceconfigurationscreen

import androidx.lifecycle.ViewModelProvider
import com.xxmrk888ytxx.coreandroid.SideEffectPortalViewModel
import com.xxmrk888ytxx.deviceconfigurationscreen.model.DeviceConfigurationUiEvent
import com.xxmrk888ytxx.deviceconfigurationscreen.model.ScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject
import javax.inject.Provider

class DeviceConfigurationViewModel @AssistedInject internal constructor(
    @Assisted private val deviceId: String,
) :
    SideEffectPortalViewModel<ScreenState, DeviceConfigurationUiEvent>(ScreenState()) {

    override fun handleEvent(event: DeviceConfigurationUiEvent) {
        TODO("Not yet implemented")
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted deviceId: String,
        ): DeviceConfigurationViewModel
    }
}