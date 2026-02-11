package com.xxmrk888ytxx.coreandroid.mvi

import kotlinx.coroutines.flow.StateFlow

/**
 * [Ru]
 * Интрефейс для держателя состояния для View
 */

/**
 * [En]
 * Interface for holder of state for View
 */
interface UiStateHolder<out STATE> {

    val state: StateFlow<STATE>
}