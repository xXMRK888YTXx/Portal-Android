package com.xxmrk888ytxx.portal.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import com.xxmrk888ytxx.portal.BuildConfig
import com.xxmrk888ytxx.portal.R
import com.xxmrk888ytxx.portal.presentation.mainActivity.MainScreenState

/**
 * Wear OS settings UI.
 *
 * It emits [SettingsEvent] for navigation, permission actions, and phone connection refresh.
 */
@Composable
fun SettingsScreen(
    state: MainScreenState,
    isPhoneConnected: Boolean?,
    onEvent: (SettingsEvent) -> Unit
) {
    BackHandler {
        onEvent(SettingsEvent.NavigateBack)
    }

    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(
                onClick = {
                    onEvent(SettingsEvent.NavigateBack)
                }
            ) {
                Text(stringResource(R.string.back))
            }
        }
    ) { contentPadding ->

        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                ListHeader(
                    transformation = SurfaceTransformation(transformationSpec),
                    modifier = Modifier
                        .transformedHeight(this, transformationSpec)
                ) {
                    Text(stringResource(R.string.settings))
                }
            }

            item {
                Button(
                    onClick = {
                        onEvent(SettingsEvent.OpenNotificationSettings)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(
                            ButtonDefaults.minimumVerticalListContentPadding
                        ),
                    transformation = SurfaceTransformation(transformationSpec),
                    label = {
                        Text(stringResource(R.string.notifications))
                    },
                    secondaryLabel = {
                        Text(
                            if (state.permissions.canPostNotifications) {
                                stringResource(R.string.enabled)
                            } else {
                                stringResource(R.string.disabled)
                            }
                        )
                    }
                )
            }

            item {
                Button(
                    onClick = {
                        onEvent(SettingsEvent.RefreshPhoneConnection)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(
                            ButtonDefaults.minimumVerticalListContentPadding
                        ),
                    transformation = SurfaceTransformation(transformationSpec),
                    label = {
                        Text(stringResource(R.string.phone_connection))
                    },
                    secondaryLabel = {
                        Text(
                            when (isPhoneConnected) {
                                true -> stringResource(R.string.connected)
                                false -> stringResource(R.string.disconnected)
                                null -> stringResource(R.string.checking)
                            }
                        )
                    }
                )
            }

            item {
                Text(
                    text = stringResource(
                        R.string.version_name,
                        "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}
