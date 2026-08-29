package com.xxmrk888ytxx.coreandroid

fun fastDebugLog(m:String) {
    AndroidLogger.debug(m)
}

fun fastDebugLog(m:Any?) {
    AndroidLogger.debug(m)
}

fun fastDebugLog(m:Throwable) {
    AndroidLogger.debug(m)
}