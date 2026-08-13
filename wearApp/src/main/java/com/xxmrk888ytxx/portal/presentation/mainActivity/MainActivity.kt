package com.xxmrk888ytxx.portal.presentation.mainActivity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SwitchButton
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import com.xxmrk888ytxx.portal.BuildConfig
import com.xxmrk888ytxx.portal.domain.model.WearProfile
import com.xxmrk888ytxx.portal.domain.model.WearTransport
import com.xxmrk888ytxx.portal.presentation.theme.PortalTheme
import javax.inject.Inject
import kotlin.math.abs

class MainActivity @Inject constructor(
    private val viewModelFactory: MainActivityViewModel.Factory
) : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            PortalTheme {
                WearApp(
                    state = state,
                    onEvent = viewModel::handleEvent
                )
            }

            LaunchedEffect(state.message) {
                state.message?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.handleEvent(MainScreenEvent.ClearMessage)
                }
            }
        }

        if (intent?.action == ACTION_OPEN_REQUEST) {
            intent.getStringExtra(EXTRA_DECISION_ID)?.let {
                viewModel.handleEvent(MainScreenEvent.OpenIncomingRequest(it))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleEvent(MainScreenEvent.OnResume)
    }

    companion object {
        const val ACTION_OPEN_REQUEST = "com.xxmrk888ytxx.portal.wear.OPEN_REQUEST"
        const val EXTRA_DECISION_ID = "decisionId"
    }
}

@Composable
private fun WearApp(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit
) {
    AppScaffold {
        when {
            !state.permissions.hasAnyPermission -> PermissionGate(state, onEvent)
            state.screen == WearScreen.Settings -> SettingsScreen(state, onEvent)
            state.screen == WearScreen.IncomingRequest -> IncomingRequestScreen(state, onEvent)
            state.selectedProfile != null -> ProfileActionsScreen(state.selectedProfile, onEvent)
            else -> ProfilesScreen(state, onEvent)
        }
    }
}

@Composable
private fun PermissionGate(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit
) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding
        ) {
            item {
                ListHeader { Text("Permissions") }
            }
            item {
                Text(
                    text = "Allow notifications or display over other apps to use Portal on the watch.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Button(
                    onClick = { onEvent(MainScreenEvent.OpenNotificationSettings) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Notifications")
                }
            }
            item {
                Button(
                    onClick = { onEvent(MainScreenEvent.OpenOverlaySettings) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Overlay")
                }
            }
            item {
                Text(
                    text = "Notifications: ${if (state.permissions.canPostNotifications) "on" else "off"}\nOverlay: ${if (state.permissions.canDrawOverlays) "on" else "off"}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ProfilesScreen(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit
) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(onClick = { onEvent(MainScreenEvent.OpenSettings) }) {
                Text("Settings")
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding
        ) {
            item {
                ListHeader { Text("PCs") }
            }
            if (state.profiles.isEmpty()) {
                item {
                    Text(
                        text = "No synced PCs",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(state.profiles) { profile ->
                    Card(
                        onClick = { onEvent(MainScreenEvent.SelectProfile(profile)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(profile.name)
                            Text(
                                text = when (profile.transport) {
                                    WearTransport.WIFI -> "Wi-Fi"
                                    WearTransport.BLUETOOTH -> "Bluetooth"
                                },
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileActionsScreen(
    profile: WearProfile,
    onEvent: (MainScreenEvent) -> Unit
) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(onClick = { onEvent(MainScreenEvent.BackToMain) }) {
                Text("Back")
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding
        ) {
            item {
                ListHeader { Text(profile.name) }
            }
            item {
                Button(
                    onClick = { onEvent(MainScreenEvent.Unlock(profile.clientId)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Unlock")
                }
            }
            if (profile.transport == WearTransport.WIFI && profile.isWakeOnLanAvailable) {
                item {
                    Button(
                        onClick = { onEvent(MainScreenEvent.WakeOnLanUnlock(profile.clientId)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Wake and unlock")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit
) {
    val listState = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(onClick = { onEvent(MainScreenEvent.BackToMain) }) {
                Text("Back")
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding
        ) {
            item { ListHeader { Text("Settings") } }
            item {
                SwitchButton(
                    checked = state.showRequestsOnLockedScreen,
                    onCheckedChange = {
                        onEvent(MainScreenEvent.SetShowRequestsOnLockedScreen(it))
                    },
                    label = { Text("Show on locked screen") },
                    secondaryLabel = {
                        Text("For incoming PC requests")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                TextButton(
                    onClick = { onEvent(MainScreenEvent.OpenNotificationSettings) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Notifications: ${if (state.permissions.canPostNotifications) "on" else "off"}")
                }
            }
            item {
                TextButton(
                    onClick = { onEvent(MainScreenEvent.OpenOverlaySettings) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Overlay: ${if (state.permissions.canDrawOverlays) "on" else "off"}")
                }
            }
            item {
                Text(
                    text = "Phone: ${if (state.profiles.isEmpty()) "no profiles" else "synced"}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun IncomingRequestScreen(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit
) {
    val request = state.incomingRequest
    if (request == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Request finished", textAlign = TextAlign.Center)
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
            text = if (request.isCompleted) "Request finished" else request.deviceName,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        if (!request.isCompleted) {
            DecisionSlider(
                onCancel = { onEvent(MainScreenEvent.CancelIncomingRequest) },
                onUnlock = { onEvent(MainScreenEvent.AllowIncomingRequest) }
            )
        } else {
            Button(onClick = { onEvent(MainScreenEvent.BackToMain) }) {
                Text("Close")
            }
        }
    }
}

@Composable
private fun DecisionSlider(
    onCancel: () -> Unit,
    onUnlock: () -> Unit
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val threshold = with(LocalDensity.current) { 52.dp.toPx() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            dragOffset > threshold -> onUnlock()
                            dragOffset < -threshold -> onCancel()
                        }
                        dragOffset = 0f
                    }
                ) { _, dragAmount ->
                    dragOffset += dragAmount
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("X", color = MaterialTheme.colorScheme.error)
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    dragOffset > 0 -> ">"
                    dragOffset < 0 -> "<"
                    else -> "Slide"
                },
                textAlign = TextAlign.Center
            )
        }
        Text("OK", color = MaterialTheme.colorScheme.primary)
    }

    Text(
        text = if (abs(dragOffset) < 1f) "Slide to decide" else "",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}
