package com.xxmrk888ytxx.addnewdevicescreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenSideEffect
import com.xxmrk888ytxx.addnewdevicescreen.model.AddNewDeviceScreenUiEvent
import com.xxmrk888ytxx.addnewdevicescreen.model.ScreenState
import com.xxmrk888ytxx.coreandroid.mvi.SideEffect
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun AddNewDeviceScreen(
    state: ScreenState,
    onEvent: (AddNewDeviceScreenUiEvent) -> Unit,
    sideEffect: Flow<AddNewDeviceScreenSideEffect>
) {
    HandleSideEffect(sideEffect) {
        when(sideEffect) {

        }
    }
    Text("AddNewDeviceScreen")
}