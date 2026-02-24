package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.deviceconfigurationscreen.contract.RemoveDeviceContract
import com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen.ProvideDeviceInfoContractImpl
import com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen.RemoveDeviceContractImpl
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
}