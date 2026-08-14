package com.xxmrk888ytxx.portal

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Intent
import androidx.core.app.AppComponentFactory
import javax.inject.Provider

/**
 * Android component factory that lets Dagger create Wear OS activities and services.
 *
 * The manifest points to this factory so constructor-injected components can be resolved from
 * [PortalApp.appComponent].
 */
class PortalComponentFactory : AppComponentFactory() {

    private lateinit var portalApplication: PortalApp

    private val activityProviders: Map<String, Provider<Activity>> by lazy {
        portalApplication.appComponent.activityProviderMap
            .mapKeys { (key, _) -> key.name }
    }

    private val serviceProviders: Map<String, Provider<Service>> by lazy {
        portalApplication.appComponent.serviceProviderMap
            .mapKeys { (key, _) -> key.name }
    }
//
//    private val broadcastReceiverProviders: Map<String, Provider<BroadcastReceiver>> by lazy {
//        portalApplication.appComponent.broadcastReceiverProviderMap
//            .mapKeys { (key, _) -> key.name }
//    }

    override fun instantiateApplicationCompat(
        cl: ClassLoader,
        className: String
    ): Application {
        portalApplication = super.instantiateApplicationCompat(cl, className) as PortalApp
        return portalApplication
    }

    override fun instantiateActivityCompat(
        cl: ClassLoader,
        className: String,
        intent: Intent?
    ): Activity = activityProviders[className]?.get() ?: super.instantiateActivityCompat(
        cl,
        className,
        intent
    )

    override fun instantiateServiceCompat(
        cl: ClassLoader,
        className: String,
        intent: Intent?
    ): Service =
        serviceProviders[className]?.get() ?: super.instantiateServiceCompat(cl, className, intent)
//
//    override fun instantiateReceiverCompat(
//        cl: ClassLoader,
//        className: String,
//        intent: Intent?
//    ): BroadcastReceiver {
//        return broadcastReceiverProviders[className]?.get() ?: super.instantiateReceiverCompat(cl, className, intent)
//    }
}
