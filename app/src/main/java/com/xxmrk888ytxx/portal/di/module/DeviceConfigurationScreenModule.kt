package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ChangeDeviceSettingsContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.RemoveDeviceContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.UpdateHostContract
import com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen.ChangeDeviceSettingsContractImpl
import com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen.ProvideDeviceInfoContractImpl
import com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen.RemoveDeviceContractImpl
import com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen.UpdateHostContractImpl
import dagger.Binds
import dagger.Module

@Module
interface DeviceConfigurationScreenModule {
    @Binds
    fun bindsProvideDeviceInfoContract(provideDeviceInfoContractImpl: ProvideDeviceInfoContractImpl): ProvideDeviceInfoContract

    @Binds
    fun bindsRemoveDeviceContract(
        removeDeviceContractImpl: RemoveDeviceContractImpl
    ) : RemoveDeviceContract

    @Binds
    fun bindChangeDeviceSettingsContract(
        changeDeviceSettingsContractImpl: ChangeDeviceSettingsContractImpl
    ) : ChangeDeviceSettingsContract

    @Binds
    fun bindsUpdateHostContractI(
        updateHostContractImpl: UpdateHostContractImpl
    ) : UpdateHostContract
}