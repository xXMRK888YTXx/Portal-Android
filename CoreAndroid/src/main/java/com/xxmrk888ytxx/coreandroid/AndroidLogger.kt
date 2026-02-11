package com.xxmrk888ytxx.coreandroid

import android.util.Log

object AndroidLogger : Logger {

    private var _isActive = true

    private const val defTag = "def"

    override fun error(m: String, tag: String?) {
        if(!_isActive) return

        Log.e(tag ?: defTag,m)
    }

    override fun error(m: Any?, tag: String?) {
        if(!_isActive) return

        Log.e(tag ?: defTag,m.toString())
    }

    override fun error(m: Throwable, tag: String?) {
        if(!_isActive) return

        Log.e(tag ?: defTag,m.stackTraceToString())
    }

    override fun info(m: String, tag: String?) {
        if(!_isActive) return

        Log.i(tag ?: defTag,m)
    }

    override fun info(m: Any?, tag: String?) {
        if(!_isActive) return

        Log.i(tag ?: defTag,m.toString())
    }

    override fun info(m: Throwable, tag: String?) {
        if(!_isActive) return

        Log.i(tag ?: defTag,m.stackTraceToString())
    }

    override fun debug(m: String, tag: String?) {
        if(!_isActive) return

        Log.d(tag ?: defTag,m)
    }

    override fun debug(m: Any?, tag: String?) {
        if(!_isActive) return

        Log.d(tag ?: defTag,m.toString())
    }

    override fun debug(m: Throwable, tag: String?) {
        if(!_isActive) return

        Log.d(tag ?: defTag,m.stackTraceToString())
    }

    override fun verbose(m: String, tag: String?) {
        if(!_isActive) return

        Log.v(tag ?: defTag,m)
    }

    override fun verbose(m: Any?, tag: String?) {
        if(!_isActive) return

        Log.d(tag ?: defTag,m.toString())
    }

    override fun verbose(m: Throwable, tag: String?) {
        if(!_isActive) return

        Log.d(tag ?: defTag,m.stackTraceToString())
    }

    override fun warm(m: String, tag: String?) {
        if(!_isActive) return

        Log.w(tag ?: defTag,m)
    }

    override fun warm(m: Any?, tag: String?) {
        if(!_isActive) return

        Log.w(tag ?: defTag,m.toString())
    }

    override fun warm(m: Throwable, tag: String?) {
        if(!_isActive) return

        Log.w(tag ?: defTag,m.stackTraceToString())
    }

    override val isActive: Boolean
        get() = _isActive

    override fun activate() {
        _isActive = true
    }

    override fun deactivate() {
        _isActive = false
    }

}