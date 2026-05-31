package com.xxmrk888ytxx.portal.domain

interface WatchDogAlarmController {
    fun scheduleRepeating(intervalMillis: Long)
    fun cancel()
}