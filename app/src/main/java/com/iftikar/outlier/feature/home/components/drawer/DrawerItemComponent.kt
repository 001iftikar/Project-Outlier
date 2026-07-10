package com.iftikar.outlier.feature.home.components.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.core.designsystem.theme.LocalSpacing
import com.iftikar.outlier.core.models.DrawerItem

@Composable
fun DrawerItemComponent(
    drawerItem: DrawerItem,
    onClick: (NavKey) -> Unit
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(spacing.roundedCornerPadding))
            .fillMaxWidth()
            .clickable(onClick = {onClick(drawerItem.navKey)})
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = drawerItem.icon,
            contentDescription = drawerItem.title
        )
        Text(
            text = drawerItem.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.offset(y = (-1).dp)
        )
    }
}