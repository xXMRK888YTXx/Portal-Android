package com.xxmrk888ytxx.goals.extensions

import android.content.Context
import com.xxmrk888ytxx.portal.PortalApp
import com.xxmrk888ytxx.portal.di.AppComponent

internal val Context.appComponent: AppComponent
    get() = when (this) {
        is PortalApp -> appComponent
        else -> applicationContext.appComponent
    }