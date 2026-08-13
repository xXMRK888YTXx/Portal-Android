package com.xxmrk888ytxx.portal.data

import android.content.Context
import com.xxmrk888ytxx.portal.domain.WearProfileRepository
import com.xxmrk888ytxx.portal.domain.model.WearProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WearProfileRepositoryImpl @Inject constructor(
    context: Context,
    private val json: Json
) : WearProfileRepository {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _profiles = MutableStateFlow(loadProfiles())
    override val profiles: StateFlow<List<WearProfile>> = _profiles.asStateFlow()

    override fun updateProfiles(profiles: List<WearProfile>) {
        preferences.edit().putString(KEY_PROFILES, json.encodeToString(profiles)).apply()
        _profiles.value = profiles
    }

    private fun loadProfiles(): List<WearProfile> = runCatching {
        val rawProfiles = preferences.getString(KEY_PROFILES, null) ?: return emptyList()
        json.decodeFromString<List<WearProfile>>(rawProfiles)
    }.getOrDefault(emptyList())

    private companion object {
        const val PREFERENCES_NAME = "wear_profiles"
        const val KEY_PROFILES = "profiles"
    }
}
