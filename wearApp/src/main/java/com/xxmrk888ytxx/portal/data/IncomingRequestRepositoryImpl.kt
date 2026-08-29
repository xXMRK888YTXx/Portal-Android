package com.xxmrk888ytxx.portal.data

import androidx.datastore.preferences.core.stringPreferencesKey
import com.xxmrk888ytxx.portal.domain.IncomingRequestRepository
import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * DataStore-backed storage for the pending incoming unlock request.
 *
 * The request is persisted so a notification tap after process recreation can still open the same
 * decision screen or show that the request was already completed.
 */
class IncomingRequestRepositoryImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage,
    private val json: Json,
    private val applicationScope: CoroutineScope
) : IncomingRequestRepository {

    private val _pendingRequest = MutableStateFlow<IncomingUnlockRequest?>(null)
    override val pendingRequest: StateFlow<IncomingUnlockRequest?> = _pendingRequest.asStateFlow()

    init {
        applicationScope.launch {
            preferencesStorage.getPropertyOrNull(KEY_PENDING_REQUEST).collect { rawRequest ->
                _pendingRequest.value = rawRequest?.let {
                    runCatching { json.decodeFromString<IncomingUnlockRequest>(it) }.getOrNull()
                }
            }
        }
    }

    override fun put(request: IncomingUnlockRequest) {
        _pendingRequest.value = request
        save(request)
    }

    override fun markCompleted(decisionId: String) {
        val current = _pendingRequest.value ?: return
        if (current.decisionId == decisionId) {
            val completed = current.copy(isCompleted = true)
            _pendingRequest.value = completed
            save(completed)
        }
    }

    override fun clear(decisionId: String) {
        if (_pendingRequest.value?.decisionId == decisionId) {
            _pendingRequest.value = null
            applicationScope.launch {
                preferencesStorage.removeProperty(KEY_PENDING_REQUEST)
            }
        }
    }

    private fun save(request: IncomingUnlockRequest) {
        applicationScope.launch {
            preferencesStorage.writeProperty(KEY_PENDING_REQUEST, json.encodeToString(request))
        }
    }

    private companion object {
        val KEY_PENDING_REQUEST = stringPreferencesKey("pending_request")
    }
}
