package com.xxmrk888ytxx.unlockservice.bluetoothService

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.unlockservice.R
import com.xxmrk888ytxx.unlockservice.core.ClientEntry
import com.xxmrk888ytxx.unlockservice.core.NetworkDriver
import com.xxmrk888ytxx.unlockservice.core.NotificationInfo
import com.xxmrk888ytxx.unlockservice.core.UnlockService
import com.xxmrk888ytxx.unlockservice.exception.PermissionDeniedException
import com.xxmrk888ytxx.unlockservice.exception.UnsupportedUnlockType
import com.xxmrk888ytxx.unlockservice.qualifier.BluetoothNetworkDriver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class BluetoothUnlockService @Inject constructor(
    @param:BluetoothNetworkDriver private val networkDriver: NetworkDriver
) : UnlockService() {

    val bluetoothManager: android.bluetooth.BluetoothManager by lazy {
        getSystemService<android.bluetooth.BluetoothManager>()
            ?: throw UnsupportedUnlockType("A device not support bluetooth!")
    }

    override val notificationInfo: NotificationInfo
        get() = NotificationInfo(222, R.string.background_service_running_bluetooth)

    override suspend fun checkPermission() {
        super.checkPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && checkSelfPermission(BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw PermissionDeniedException(listOf(BLUETOOTH_CONNECT))
        }
    }

    override suspend fun waitConnectionToNetwork() {
        bluetoothStateFlow().first { it == BluetoothAdapter.STATE_ON }
    }

    override suspend fun connect(
        clientId: String,
        clientEntry: ClientEntry
    ) {
        networkDriver.connect(
            messagesForSendChannel = clientEntry.sendMessagesChannel,
            receivedRequestChannel = clientEntry.unlockRequests,
            clientId = clientId
        )
    }

    fun Context.bluetoothStateFlow(): Flow<Int> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.STATE_OFF
                    )
                    trySend(state)
                }
            }
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(receiver, filter)

        awaitClose {
            unregisterReceiver(receiver)
        }
    }.onStart {
        val adapter = bluetoothManager.adapter
        emit(adapter?.state ?: BluetoothAdapter.STATE_OFF)
    }
}