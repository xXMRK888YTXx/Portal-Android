package com.xxmrk888ytxx.portal.di.key

import android.content.BroadcastReceiver
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class BroadcastReceiverKey(val value: KClass<out BroadcastReceiver>)