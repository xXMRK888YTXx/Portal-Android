package com.xxmrk888ytxx.portal.data

import android.content.Context
import com.xxmrk888ytxx.portal.domain.IncomingRequestRepository
import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import javax.inject.Inject

class IncomingRequestRepositoryImpl @Inject constructor(
    context: Context,
    private val json: Json
) : IncomingRequestRepository {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _pendingRequest = MutableStateFlow(loadPendingRequest())
    override val pendingRequest: StateFlow<IncomingUnlockRequest?> = _pendingRequest.asStateFlow()

    override fun put(request: IncomingUnlockRequest) {
        save(request)
        _pendingRequest.value = request
    }

    override fun markCompleted(decisionId: String) {
        val current = _pendingRequest.value ?: return
        if (current.decisionId == decisionId) {
            val completed = current.copy(isCompleted = true)
            save(completed)
            _pendingRequest.value = completed
        }
    }

    override fun clear(decisionId: String) {
        if (_pendingRequest.value?.decisionId == decisionId) {
            preferences.edit().remove(KEY_PENDING_REQUEST).apply()
            _pendingRequest.value = null
        }
    }

    private fun save(request: IncomingUnlockRequest) {
        preferences.edit()
            .putString(KEY_PENDING_REQUEST, json.encodeToString(request))
            .apply()
    }

    private fun loadPendingRequest(): IncomingUnlockRequest? = runCatching {
        val rawRequest = preferences.getString(KEY_PENDING_REQUEST, null) ?: return null
        json.decodeFromString<IncomingUnlockRequest>(rawRequest)
    }.getOrNull()

    private companion object {
        const val PREFERENCES_NAME = "incoming_unlock_requests"
        const val KEY_PENDING_REQUEST = "pending_request"
    }
}
