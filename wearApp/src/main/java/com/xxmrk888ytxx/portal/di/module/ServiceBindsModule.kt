package com.xxmrk888ytxx.portal.di.module

import android.app.Service
import com.xxmrk888ytxx.portal.data.service.WearPortalListenerService
import com.xxmrk888ytxx.portal.di.key.ServiceKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ServiceBindsModule {
    @Binds
    @IntoMap
    @ServiceKey(WearPortalListenerService::class)
    fun bindsWearPortalListenerService(
        wearPortalListenerService: WearPortalListenerService
    ): Service
}
