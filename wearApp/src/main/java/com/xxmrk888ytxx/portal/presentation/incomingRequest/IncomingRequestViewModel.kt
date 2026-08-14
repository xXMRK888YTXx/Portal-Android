package com.xxmrk888ytxx.portal.presentation.incomingRequest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.portal.data.WearDecisionPayloadValue
import com.xxmrk888ytxx.portal.domain.IncomingRequestRepository
import com.xxmrk888ytxx.portal.domain.WearPhoneGateway
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * MVI ViewModel for incoming unlock requests shown on the watch.
 *
 * It observes the locally stored pending request and sends cancel/unlock decisions back to the
 * phone. The phone decides whether this is the first accepted decision for the request.
 */
class IncomingRequestViewModel @Inject constructor(
    private val incomingRequestRepository: IncomingRequestRepository,
    private val wearPhoneGateway: WearPhoneGateway
) : ViewModel() {

    val request = incomingRequestRepository.pendingRequest.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _sideEffect = MutableSharedFlow<IncomingRequestSideEffect>(extraBufferCapacity = 4)
    val sideEffect: SharedFlow<IncomingRequestSideEffect> = _sideEffect.asSharedFlow()

    fun handleEvent(event: IncomingRequestEvent) {
        when (event) {
            IncomingRequestEvent.NavigateBack -> {
                _sideEffect.tryEmit(IncomingRequestSideEffect.NavigateBack)
            }

            IncomingRequestEvent.Cancel -> resolve(WearDecisionPayloadValue.CANCEL)
            IncomingRequestEvent.Unlock -> resolve(WearDecisionPayloadValue.UNLOCK)
        }
    }

    private fun resolve(decision: WearDecisionPayloadValue) {
        val current = request.value ?: return
        viewModelScope.launch {
            runCatching { wearPhoneGateway.sendDecision(current.decisionId, decision) }
                .onSuccess {
                    incomingRequestRepository.clear(current.decisionId)
                    _sideEffect.tryEmit(IncomingRequestSideEffect.NavigateBack)
                }
                .onFailure { _sideEffect.tryEmit(IncomingRequestSideEffect.ShowDecisionError) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<IncomingRequestViewModel>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}
