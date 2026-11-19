package com.iftikar.outlier.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val searchMode: String,
    val matchedTerms: List<String>,
    val posts: List<PostResponse>,
    val matchedTechStackInstead: List<MatchedTechStackInstead>,
    val nextCursor: String?,
    val hasMore: Boolean,
    val nextMatchedTechStackInsteadCursor: String?,
    val hasMoreMatchedTechStackInstead: Boolean
)

@Serializable
data class MatchedTechStackInstead(
    val post: PostResponse,
    val matchedTechStack: List<String>
)
