package com.iftikar.outlier.core.network.model

import com.iftikar.outlier.core.models.DrawerUserInfo
import kotlinx.serialization.Serializable

@Serializable
data class DrawerUserInfoDto(
    val name: String,
    val isDeveloper: Boolean
)

fun DrawerUserInfoDto.esExternalModel(): DrawerUserInfo {
    return DrawerUserInfo(
        name = name,
        isDeveloper = isDeveloper
    )
}
