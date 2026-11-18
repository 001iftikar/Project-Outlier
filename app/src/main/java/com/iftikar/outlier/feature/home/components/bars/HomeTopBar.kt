package com.iftikar.outlier.feature.home.components.bars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.core.designsystem.component.bars.OutlierTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    isDeveloper: Boolean,
    onCreatePostClick: () -> Unit,
    onHeartClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior
) {
    OutlierTopAppBar(
        title = {},
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (isDeveloper) {
                    IconButton(
                        onClick = onCreatePostClick
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Create post"
                        )
                    }
                } else {
                    Spacer(Modifier.size(48.dp))
                }

                Text(
                    text = "Outlier",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Cursive
                )

                IconButton(
                    onClick = onHeartClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Notifications"
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}