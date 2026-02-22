package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.mainscreen.contract.ProvideSavedDevices
import com.xxmrk888ytxx.mainscreen.contract.SendUnlockRequestContract
import com.xxmrk888ytxx.portal.providedContract.mainScreen.ProvideSavedDevicesImpl
import com.xxmrk888ytxx.portal.providedContract.mainScreen.SendUnlockRequestContractImpl
import dagger.Binds
import dagger.Module

@Module
interface MainScreenModule {
    @Binds
    fun bindsProvideSavedDevices(
        provideSavedDevicesImpl: ProvideSavedDevicesImpl
    ) : ProvideSavedDevices

    @Binds
    fun bindsSendUnlockRequestContract(
        sendUnlockRequestContractImpl: SendUnlockRequestContractImpl
    ) : SendUnlockRequestContract
}