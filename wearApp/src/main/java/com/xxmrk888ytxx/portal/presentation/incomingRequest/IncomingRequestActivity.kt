package com.xxmrk888ytxx.portal.presentation.incomingRequest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.TimeText
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.presentation.theme.PortalTheme
import javax.inject.Inject

class IncomingRequestActivity @Inject constructor(
    private val incomingRequestViewModelFactory: IncomingRequestViewModel.Factory
) : ComponentActivity() {

    private val incomingRequestViewModel by viewModels<IncomingRequestViewModel> {
        incomingRequestViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        setContent {
            val incomingRequest by incomingRequestViewModel.request.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                incomingRequestViewModel.sideEffect.collect { effect ->
                    when (effect) {
                        IncomingRequestSideEffect.NavigateBack -> finish()
                        IncomingRequestSideEffect.ShowDecisionError -> {
                            Toast.makeText(
                                this@IncomingRequestActivity,
                                getString(R.string.failed_to_send_decision),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            PortalTheme {
                AppScaffold(
                    timeText = { TimeText() }
                ) {
                    IncomingRequestScreen(
                        request = incomingRequest,
                        onEvent = incomingRequestViewModel::handleEvent
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    }

    companion object {
        const val EXTRA_DECISION_ID = "decisionId"
    }
}
