package com.xxmrk888ytxx.portal.di.key

import android.app.Service
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Dagger multibinding key for Service constructor injection.
 */
@MapKey
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceKey(val value: KClass<out Service>)
