package com.xxmrk888ytxx.portal.presentation.incomingRequest

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.domain.model.IncomingUnlockRequest

/**
 * Incoming unlock request UI with explicit cancel and confirm buttons.
 *
 * This is opened from both normal app navigation and notification taps. User actions are emitted as
 * [IncomingRequestEvent].
 */
@Composable
fun IncomingRequestScreen(
    request: IncomingUnlockRequest?,
    onEvent: (IncomingRequestEvent) -> Unit
) {
    BackHandler { onEvent(IncomingRequestEvent.NavigateBack) }
    val currentRequest = request

    if (currentRequest == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.request_finished), textAlign = TextAlign.Center)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (currentRequest.isCompleted) {
                stringResource(R.string.request_finished)
            } else {
                currentRequest.deviceName
            },
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        if (!currentRequest.isCompleted) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onEvent(IncomingRequestEvent.Cancel)
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Text(
                        text = stringResource(R.string.cancel_icon),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Button(
                    onClick = {
                        onEvent(IncomingRequestEvent.Unlock)
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Text(
                        text = stringResource(R.string.confirm_icon),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        } else {
            Button(onClick = { onEvent(IncomingRequestEvent.NavigateBack) }) {
                Text(stringResource(R.string.close))
            }
        }
    }
}
