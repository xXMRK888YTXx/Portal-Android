package com.xxmrk888ytxx.coreandroid

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

suspend inline fun <R> runCatching(
    context: CoroutineContext,
    writeErrorInLog: Boolean = true,
    crossinline onMapException: (Throwable) -> Throwable = { it },
    noinline block: suspend CoroutineScope.() -> R
): Result<R> = withContext(context) {
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    }
    catch (e: Throwable) {
        if (writeErrorInLog) fastDebugLog(e)
        Result.failure(onMapException(e))
    }
}