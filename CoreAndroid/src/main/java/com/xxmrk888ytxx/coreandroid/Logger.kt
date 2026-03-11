package com.xxmrk888ytxx.coreandroid

import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Logger {
    val isActive: Boolean
    val logs: StateFlow<List<String>>

    //String
    fun error(m: String, tag: String? = null)

    fun info(m: String, tag: String? = null)

    fun debug(m: String, tag: String? = null)

    fun verbose(m: String, tag: String? = null)

    fun warm(m: String, tag: String? = null)

    //Any
    fun error(m: Any?, tag: String? = null)

    fun info(m: Any?, tag: String? = null)

    fun debug(m: Any?, tag: String? = null)

    fun verbose(m: Any?, tag: String? = null)

    fun warm(m: Any?, tag: String? = null)

    //Exception

    fun error(m: Throwable, tag: String? = null)

    fun info(m: Throwable, tag: String? = null)

    fun debug(m: Throwable, tag: String? = null)

    fun verbose(m: Throwable, tag: String? = null)

    fun warm(m: Throwable, tag: String? = null)

    fun activate()

    fun deactivate()
}