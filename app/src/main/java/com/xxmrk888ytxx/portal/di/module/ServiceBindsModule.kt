package com.xxmrk888ytxx.portal.di.module

import android.app.Service
import com.xxmrk888ytxx.portal.di.key.ServiceKey
import com.xxmrk888ytxx.unlockservice.wifiService.WifiUnlockService
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ServiceBindsModule {
    @Binds
    @IntoMap
    @ServiceKey(WifiUnlockService::class)
    fun bindMainActivityToActivityForMultiBinding(wifiUnlockService: WifiUnlockService): Service
}