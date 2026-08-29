package com.xxmrk888ytxx.coreandroid

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object AndroidLogger : Logger {

    private var _isActive = true

    private const val defTag = "def"

    private val _lastLogId = MutableStateFlow(0L)

    private val scope = CoroutineScope(Dispatchers.Default.limitedParallelism(1) + SupervisorJob())

    private val _logs = MutableStateFlow<List<Pair<Long,String>>>(emptyList())
    override val logs: StateFlow<List<Pair<Long,String>>> = _logs.asStateFlow()

    override fun error(m: String, tag: String?) {
        if(!_isActive) return

        Log.e(tag ?: defTag,m)
        writeInRamLog(m)
    }

    override fun error(m: Any?, tag: String?) {
        if(!_isActive) return

        Log.e(tag ?: defTag,m.toString())
        writeInRamLog(m)
    }

    override fun error(m: Throwable, tag: String?) {
        if(!_isActive) return

        Log.e(tag ?: defTag,m.stackTraceToString())
        writeInRamLog(m)
    }

    override fun info(m: String, tag: String?) {
        if(!_isActive) return

        Log.i(tag ?: defTag,m)
        writeInRamLog(m)
    }

    override fun info(m: Any?, tag: String?) {
        if(!_isActive) return

        Log.i(tag ?: defTag,m.toString())
        writeInRamLog(m)
    }

    override fun info(m: Throwable, tag: String?) {
        if(!_isActive) return

        Log.i(tag ?: defTag,m.stackTraceToString())
        writeInRamLog(m)
    }

    override fun debug(m: String, tag: String?) {
        if(!_isActive) return

        Log.d(tag ?: defTag,m)
        writeInRamLog(m)
    }

    override fun debug(m: Any?, tag: String?) {
        if(!_isActive) return

        Log.d(tag ?: defTag,m.toString())
        writeInRamLog(m)
    }

    override fun debug(m: Throwable, tag: String?) {
        if(!_isActive) return

        Log.d(tag ?: defTag,m.stackTraceToString())
        writeInRamLog(m)
    }

    override fun verbose(m: String, tag: String?) {
        if(!_isActive) return

        Log.v(tag ?: defTag,m)
        writeInRamLog(m)
    }

    override fun verbose(m: Any?, tag: String?) {
        if(!_isActive) return

        Log.d(tag ?: defTag,m.toString())
        writeInRamLog(m)
    }

    override fun verbose(m: Throwable, tag: String?) {
        if(!_isActive) return

        Log.d(tag ?: defTag,m.stackTraceToString())
        writeInRamLog(m)
    }

    override fun warm(m: String, tag: String?) {
        if(!_isActive) return

        Log.w(tag ?: defTag,m)
        writeInRamLog(m)
    }

    override fun warm(m: Any?, tag: String?) {
        if(!_isActive) return

        Log.w(tag ?: defTag,m.toString())
        writeInRamLog(m)
    }

    override fun warm(m: Throwable, tag: String?) {
        if(!_isActive) return

        Log.w(tag ?: defTag,m.stackTraceToString())
        writeInRamLog(m)
    }

    override val isActive: Boolean
        get() = _isActive

    override fun activate() {
        _isActive = true
    }

    override fun deactivate() {
        _isActive = false
    }

    override fun clearLogs() {
        _logs.update { emptyList() }
    }

    private fun writeInRamLog(m: Any?) = scope.launch {
        val message = when(m) {
            is Throwable -> m.stackTraceToString()
            else -> m.toString()
        }

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss")
        val timeString = current.format(formatter)

        val newEntry = _lastLogId.value to "[ $timeString ] $message\n"
        _lastLogId.value += 1
        _logs.value = listOf(newEntry) + _logs.value
    }

}