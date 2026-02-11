package com.xxmrk888ytxx.coreandroid.mvi

/**
 * [Ru]
 * Интерфейс для обработчика [UiEvent]
 */

/**
 * [En]
 * Interface for handler of [UiEvent]
 */
interface UiEventHandler<EVENT : UiEvent> {
    fun handleEvent(event: EVENT)
}