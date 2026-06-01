package com.xxmrk888ytxx.portal.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xxmrk888ytxx.coreandroid.PortalViewModel
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.coreandroid.mvi.SideEffectSender
import com.xxmrk888ytxx.coreandroid.mvi.UiEvent
import com.xxmrk888ytxx.portal.PortalApp
import com.xxmrk888ytxx.portal.di.AppComponent
import com.xxmrk888ytxx.portal.domain.BiometricActivityResultReceiver
import com.xxmrk888ytxx.portal.domain.BiometricDialogController
import com.xxmrk888ytxx.portal.domain.MdnsManager
import com.xxmrk888ytxx.portal.domain.model.BiometricAuthResult
import com.xxmrk888ytxx.portal.domain.model.BiometricDialogEvent
import com.xxmrk888ytxx.portal.domain.model.UnlockMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Provider
import com.xxmrk888ytxx.deviceconfigurationscreen.model.UnlockMethod as ConfigurationScreenUnlockMethod


internal val Context.appComponent: AppComponent
    get() = when (this) {
        is PortalApp -> appComponent
        else -> applicationContext.appComponent
    }

@Composable
inline fun <STATE, EVENT : UiEvent, PVM : PortalViewModel<STATE, EVENT>> ScreenContent(
    content: @Composable (state: STATE, onEvent: (EVENT) -> Unit) -> Unit,
    portalViewModelFactory: Provider<PVM>
) {
    val viewModel: PVM = viewModel { portalViewModelFactory.get() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    content(state, viewModel::handleEvent)
}

@Composable
inline fun <STATE, EVENT : UiEvent, PVM> ScreenContent(
    content: @Composable (state: STATE, onEvent: (EVENT) -> Unit, sideEffect: Flow<SideEffect>) -> Unit,
    portalViewModelFactory: Provider<PVM>
) where PVM : PortalViewModel<STATE, EVENT>, PVM : SideEffectSender<SideEffect> {
    val viewModel: PVM = viewModel { portalViewModelFactory.get() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    content(state, viewModel::handleEvent, viewModel.effect)
}

@Composable
inline fun <STATE, EVENT : UiEvent, reified PVM> ScreenContent(
    content: @Composable (state: STATE, onEvent: (EVENT) -> Unit, sideEffect: Flow<SideEffect>) -> Unit,
    crossinline portalViewModelFactory: () -> PVM
) where PVM : PortalViewModel<STATE, EVENT>, PVM : SideEffectSender<SideEffect> {
    val viewModel: PVM = viewModel { portalViewModelFactory.invoke() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    content(state, viewModel::handleEvent, viewModel.effect)
}

fun FragmentActivity.collectBiometricAuthResult(
    biometricActivityResultReceiver: BiometricActivityResultReceiver,
    biometricDialogController: BiometricDialogController
) =
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            biometricActivityResultReceiver.biometricAuthRequestForActivity.collect { option ->
                biometricDialogController.sendRequest(
                    activity = this@collectBiometricAuthResult,
                    onEvent = {
                        when (it) {
                            BiometricDialogEvent.Success -> biometricActivityResultReceiver.onNewBiometricAuthResult(
                                BiometricAuthResult.Success
                            )

                            BiometricDialogEvent.Error, BiometricDialogEvent.Canceled -> biometricActivityResultReceiver.onNewBiometricAuthResult(
                                BiometricAuthResult.Failed
                            )

                            BiometricDialogEvent.Failed -> {}
                        }
                    },
                    description = option.description
                )
            }
        }
    }

@Suppress("DEPRECATION")
fun <T> Intent.getParsableExtraCompat(name: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(name, clazz)
    } else {
        getParcelableExtra(name)
    }
}

suspend fun MdnsManager.waitHostForClient(
    clientId: String,
): String? = withTimeoutOrNull(MDSN_DISCOVERY_TIMEOUT) {
    foundedHosts.first { it.containsKey(clientId) }[clientId]?.hostIp
}

private const val MDSN_DISCOVERY_TIMEOUT = 3000L

fun ConfigurationScreenUnlockMethod.toDomainModel(): UnlockMethod = when (this) {
    is ConfigurationScreenUnlockMethod.Automatic -> UnlockMethod.Automatic
    is ConfigurationScreenUnlockMethod.ConfirmationScreen -> UnlockMethod.ConfirmationScreen
    is ConfigurationScreenUnlockMethod.Notification -> UnlockMethod.Notification
}