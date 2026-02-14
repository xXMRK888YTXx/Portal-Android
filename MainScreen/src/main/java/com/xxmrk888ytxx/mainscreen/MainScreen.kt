package com.xxmrk888ytxx.mainscreen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.xxmrk888ytxx.corecompose.HandleSideEffect
import com.xxmrk888ytxx.corecompose.LocalNavigator
import com.xxmrk888ytxx.mainscreen.model.MainScreenEvent
import com.xxmrk888ytxx.mainscreen.model.MainScreenSideEffect
import com.xxmrk888ytxx.mainscreen.model.ScreenState
import kotlinx.coroutines.flow.Flow

@Composable
fun MainScreen(
    screenState: ScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    sideEffect: Flow<MainScreenSideEffect>
) {
    val navigator = LocalNavigator.current
    HandleSideEffect(sideEffect) { effect ->
        when(effect) {
            MainScreenSideEffect.NavigateToAddNewDeviceScreen -> navigator.fromMainScreenToAddNewDeviceScreen()
        }
    }
    Scaffold(
        Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(MainScreenEvent.AddNewDevice) }
            ) {
                Icon(painter = painterResource(R.drawable.outline_add), contentDescription = null)
            }
        },
        contentWindowInsets = WindowInsets()
    ) { paddingValues ->
        LazyColumn(Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            item {
                Text("MainScreen")
            }
        }
    }
}