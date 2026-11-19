package com.iftikar.outlier.core.models

data class SearchResult(
    val searchMode: String,
    val matchedTerms: List<String>,
    val posts: List<Post>,
    val matchedTechStackInstead: List<Post>,
    val nextCursor: String?,
    val hasMore: Boolean,
    val nextMatchedTechStackInsteadCursor: String?,
    val hasMoreMatchedTechStackInstead: Boolean
)
