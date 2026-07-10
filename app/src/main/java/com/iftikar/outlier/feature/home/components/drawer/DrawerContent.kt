package com.iftikar.outlier.feature.home.components.drawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.core.designsystem.theme.LocalSpacing
import com.iftikar.outlier.core.localbase.drawerItems

@Composable
fun DrawerContent(
    drawerState: DrawerState,
    onDrawerItemClick: (NavKey) -> Unit
) {
    val spacing = LocalSpacing.current
    DismissibleDrawerSheet(
        drawerState = drawerState,
        drawerContainerColor = MaterialTheme.colorScheme.surfaceDim
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = spacing.horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Hi, Ryu",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(Modifier.height(24.dp))

            drawerItems.forEach { item ->
                DrawerItemComponent(
                    drawerItem = item,
                    onClick = onDrawerItemClick
                )
            }
        }
    }
}