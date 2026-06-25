package com.iftikar.outlier.core.models

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

data class DrawerItem(
    val icon: ImageVector,
    val title: String,
    val navKey: NavKey
)
