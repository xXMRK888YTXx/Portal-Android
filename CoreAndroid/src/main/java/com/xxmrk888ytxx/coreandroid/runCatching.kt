package com.xxmrk888ytxx.coreandroid

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

suspend inline fun <R> runCatching(
    context: CoroutineContext,
    noinline block: suspend CoroutineScope.() -> R
): Result<R> = runCatching {
    withContext(context, block)
}