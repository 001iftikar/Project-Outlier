package com.iftikar.outlier.feature.shared

import com.iftikar.outlier.core.models.DrawerUserInfo

data class DrawerUserInfoState(
    val isDrawerLoading: Boolean = false,
    val drawerUserInfo: DrawerUserInfo? = null
)
