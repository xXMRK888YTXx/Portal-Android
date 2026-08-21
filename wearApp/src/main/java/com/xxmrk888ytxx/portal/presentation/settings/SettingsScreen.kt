package com.xxmrk888ytxx.portal.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.Icon
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
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_notifications),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(stringResource(R.string.notifications))
                    },
                    secondaryLabel = {
                        Text(
                            text = if (state.permissions.canPostNotifications) {
                                stringResource(R.string.enabled)
                            } else {
                                stringResource(R.string.disabled)
                            },
                            color = if (state.permissions.canPostNotifications) {
                                com.xxmrk888ytxx.portal.presentation.theme.StatusConnectedColor
                            } else {
                                com.xxmrk888ytxx.portal.presentation.theme.StatusDisconnectedColor
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
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_phone),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(stringResource(R.string.phone_connection))
                    },
                    secondaryLabel = {
                        Text(
                            text = when (isPhoneConnected) {
                                true -> stringResource(R.string.connected)
                                false -> stringResource(R.string.disconnected)
                                null -> stringResource(R.string.checking)
                            },
                            color = when (isPhoneConnected) {
                                true -> com.xxmrk888ytxx.portal.presentation.theme.StatusConnectedColor
                                false -> com.xxmrk888ytxx.portal.presentation.theme.StatusDisconnectedColor
                                null -> com.xxmrk888ytxx.portal.presentation.theme.StatusCheckingColor
                            }
                        )
                    }
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp)
                        .transformedHeight(this, transformationSpec),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(
                            R.string.version_name,
                            if (BuildConfig.DEBUG) {
                                "${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE} (${BuildConfig.VERSION_CODE})"
                            } else {
                                "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                            }
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
