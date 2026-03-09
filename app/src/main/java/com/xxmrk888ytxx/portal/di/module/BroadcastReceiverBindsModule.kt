package com.xxmrk888ytxx.portal.di.module

import android.app.Service
import android.content.BroadcastReceiver
import com.xxmrk888ytxx.portal.data.broadcastReceiver.ShortcutPinnedReceiver
import com.xxmrk888ytxx.portal.di.key.BroadcastReceiverKey
import com.xxmrk888ytxx.portal.di.key.ServiceKey
import com.xxmrk888ytxx.unlockservice.wifiService.WifiUnlockService
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface BroadcastReceiverBindsModule {
    @Binds
    @IntoMap
    @BroadcastReceiverKey(ShortcutPinnedReceiver::class)
    fun bindMainActivityToActivityForMultiBinding(shortcutPinnedReceiver: ShortcutPinnedReceiver): BroadcastReceiver
}