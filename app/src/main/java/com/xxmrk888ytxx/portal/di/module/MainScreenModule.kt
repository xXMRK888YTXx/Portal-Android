package com.xxmrk888ytxx.portal.di.module

import com.xxmrk888ytxx.mainscreen.contract.CreateShortcutContract
import com.xxmrk888ytxx.mainscreen.contract.PermissionContract
import com.xxmrk888ytxx.mainscreen.contract.ProvideSavedDevices
import com.xxmrk888ytxx.mainscreen.contract.SendUnlockRequestContract
import com.xxmrk888ytxx.portal.providedContract.mainScreen.CreateShortcutContractImpl
import com.xxmrk888ytxx.portal.providedContract.mainScreen.PermissionContractImpl
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

    @Binds
    fun bindsCreateShortcutContract(
        createShortcutContractImpl: CreateShortcutContractImpl
    ): CreateShortcutContract

    @Binds
    fun bindsProvidePermissionState(
        providePermissionStateImpl: PermissionContractImpl
    ) : PermissionContract
}