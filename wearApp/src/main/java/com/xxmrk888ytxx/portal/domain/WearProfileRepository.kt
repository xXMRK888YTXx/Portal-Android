package com.xxmrk888ytxx.portal.domain

import com.xxmrk888ytxx.portal.domain.model.WearProfile
import kotlinx.coroutines.flow.StateFlow

interface WearProfileRepository {
    val profiles: StateFlow<List<WearProfile>>
    fun updateProfiles(profiles: List<WearProfile>)
}
