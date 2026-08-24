package com.iftikar.outlier.core.models

/**
 * Show the name of the user on the drawer and decide whether to show the create post button based on role
 */
data class DrawerUserInfo(
    val name: String,
    val isDeveloper: Boolean
)