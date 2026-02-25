package com.xxmrk888ytxx.portal.di.key

import android.app.Activity
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityKey(val value: KClass<out Activity>)
