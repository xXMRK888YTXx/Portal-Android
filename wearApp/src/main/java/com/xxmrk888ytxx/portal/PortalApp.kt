package com.xxmrk888ytxx.portal

import android.app.Application
import com.xxmrk888ytxx.portal.di.AppComponent
import com.xxmrk888ytxx.portal.di.DaggerAppComponent

/**
 * Application entry point for the Wear OS module.
 *
 * Owns the app-scoped Dagger component used by the custom [PortalComponentFactory] to instantiate
 * injected activities and services.
 */
class PortalApp : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

}
