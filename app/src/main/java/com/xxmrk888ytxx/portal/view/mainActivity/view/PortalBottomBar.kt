package com.xxmrk888ytxx.portal.view.mainActivity.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.corecompose.uiText.asString
import com.xxmrk888ytxx.portal.view.mainActivity.model.PortalBottomBarItem

@Composable
fun PortalBottomBar(
    items: List<PortalBottomBarItem>,
    selectedItemIndex: Int,
    onItemClicked: (PortalBottomBarItem) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = selectedItemIndex == item.id,
                    onClick = { onItemClicked(item) },
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(text = item.text.asString())
                    },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White
                    )
                )
            }
        }
    }
}
