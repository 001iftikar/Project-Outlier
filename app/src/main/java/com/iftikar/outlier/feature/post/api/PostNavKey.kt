package com.iftikar.outlier.feature.post.api

import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.post.impl.StateToSurvive
import kotlinx.serialization.Serializable

@Serializable
data class PostNavKey(
    val stateToSurvive: StateToSurvive? = null
) : NavKey
