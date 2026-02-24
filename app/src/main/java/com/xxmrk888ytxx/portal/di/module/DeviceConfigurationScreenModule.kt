package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.deviceconfigurationscreen.contract.ProvideDeviceInfoContract
import com.xxmrk888ytxx.portal.providedContract.deviceConfigurationScreen.ProvideDeviceInfoContractImpl
import dagger.Binds
import dagger.Module

@Module
interface DeviceConfigurationScreenModule {
    @Binds
    fun bindsProvideDeviceInfoContract(provideDeviceInfoContractImpl: ProvideDeviceInfoContractImpl): ProvideDeviceInfoContract
}