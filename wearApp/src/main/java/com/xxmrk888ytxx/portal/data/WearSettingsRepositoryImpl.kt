package com.xxmrk888ytxx.portal.data

import android.content.Context
import com.xxmrk888ytxx.portal.domain.WearSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class WearSettingsRepositoryImpl @Inject constructor(
    context: Context
) : WearSettingsRepository {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _showRequestsOnLockedScreen = MutableStateFlow(
        preferences.getBoolean(KEY_SHOW_REQUESTS_ON_LOCKED_SCREEN, false)
    )
    override val showRequestsOnLockedScreen: StateFlow<Boolean> =
        _showRequestsOnLockedScreen.asStateFlow()

    override fun setShowRequestsOnLockedScreen(value: Boolean) {
        preferences.edit().putBoolean(KEY_SHOW_REQUESTS_ON_LOCKED_SCREEN, value).apply()
        _showRequestsOnLockedScreen.value = value
    }

    private companion object {
        const val PREFERENCES_NAME = "wear_settings"
        const val KEY_SHOW_REQUESTS_ON_LOCKED_SCREEN = "show_requests_on_locked_screen"
    }
}
