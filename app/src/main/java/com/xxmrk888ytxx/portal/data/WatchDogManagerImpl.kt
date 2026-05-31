package com.xxmrk888ytxx.portal.data

import com.xxmrk888ytxx.portal.domain.SettingsRepository
import com.xxmrk888ytxx.portal.domain.WatchDogAlarmController
import com.xxmrk888ytxx.portal.domain.WatchDogManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class WatchDogManagerImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val alarmController: WatchDogAlarmController,
) : WatchDogManager {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var observeJob: Job? = null

    override fun startObserve() {
        if (observeJob?.isActive == true) return

        observeJob = coroutineScope.launch(Dispatchers.Default) {
            settingsRepository.portalSettings
                .map { it.isWatchDogEnabled }
                .distinctUntilChanged()
                .collect { isEnabled ->
                    if (isEnabled) {
                        alarmController.scheduleRepeating(WATCH_DOG_INTERVAL_MILLIS)
                    } else {
                        alarmController.cancel()
                    }
                }
        }
    }

    companion object {
        const val WATCH_DOG_INTERVAL_MILLIS = 30_000L
    }
}
