package com.xxmrk888ytxx.portal.di.module

import android.content.BroadcastReceiver
import com.xxmrk888ytxx.portal.data.broadcastReceiver.WearNotificationActionReceiver
import com.xxmrk888ytxx.portal.di.key.BroadcastReceiverKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface BroadcastReceiverBindsModule {

    @Binds
    @IntoMap
    @BroadcastReceiverKey(WearNotificationActionReceiver::class)
    fun bindWearNotificationActionReceiver(
        wearNotificationActionReceiver: WearNotificationActionReceiver
    ): BroadcastReceiver
}
