package com.xxmrk888ytxx.coreandroid.mvi

/**
 * [Ru]
 * Интрефейс для объединения [UiEventHandler] и [UiEventHandler]
 */

/**
 * [En]
 *  Interface for join [UiEventHandler] and [UiEventHandler]
 */
interface UiModel<STATE, EVENT : UiEvent> : UiEventHandler<EVENT>, UiStateHolder<STATE>