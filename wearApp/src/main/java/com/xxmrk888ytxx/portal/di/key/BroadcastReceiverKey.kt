package com.xxmrk888ytxx.portal.di.key

import android.content.BroadcastReceiver
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
annotation class BroadcastReceiverKey(val value: KClass<out BroadcastReceiver>)
