package com.xxmrk888ytxx.addnewdevicescreen.model

sealed interface BluetoothState {
    data object Disabled : BluetoothState
    data object PermissionDenied : BluetoothState
    data object NotSupported : BluetoothState
    data class Success(val pairedDevices: List<BluetoothDevice>) : BluetoothState
}