package com.xxmrk888ytxx.coreandroid

import android.os.Handler
import android.os.Looper

private val handler by lazy {
    Handler(Looper.getMainLooper())
}

fun runOnUiThread(action:() -> Unit) : Unit {
    handler.post(action)
}