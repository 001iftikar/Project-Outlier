package com.iftikar.outlier.feature.home.impl

import com.iftikar.outlier.core.models.Comment
import kotlin.uuid.Uuid

data class PostCommentState(
    val comments: List<Comment> = emptyList(),
    val commentsCount: String = "",
    val cursor: String? = null,
    val isLoading: Boolean = true,
    val isPaging: Boolean = false,
    val error: String? = null,
    val optimisticComments: List<OptimisticComment> = emptyList(),
    val comment: String = ""
)


data class OptimisticComment(
    val id: String = Uuid.random().toString(),
    val sort: Int,
    val author: String,
    val content: String,
    val updatedAt: String

)

fun OptimisticComment.toRealComment() = Comment(
    id = id,
    authorName = author,
    content = content,
    updatedAt = updatedAt
)
sealed interface PostCommentAction {
    data class OnCommentChange(val comment: String) : PostCommentAction
    data class DoComment(val postId: String, val author: String) : PostCommentAction
    data class OnLoadMore(val postId: String) : PostCommentAction
}

sealed interface PostCommentEvent {
    data object OnCommentAdded : PostCommentEvent
    data class ShowError(val err: String) : PostCommentEvent
}