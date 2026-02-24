package com.xxmrk888ytxx.coreandroid

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

suspend inline fun <R> runCatching(
    context: CoroutineContext,
    writeErrorInLog: Boolean = true,
    noinline block: suspend CoroutineScope.() -> R
): Result<R> = runCatching {
    withContext(context, block)
}.onFailure { if (writeErrorInLog) fastDebugLog(it) }