package com.iftikar.outlier.feature.home.components.comment

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iftikar.outlier.core.designsystem.component.input.TextFieldComponent
import com.iftikar.outlier.core.models.Comment
import com.iftikar.outlier.feature.home.impl.OptimisticComment
import com.iftikar.outlier.feature.home.impl.PostCommentEvent
import com.iftikar.outlier.feature.home.impl.toRealComment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    comments: List<Comment>,
    optimisticComments: List<OptimisticComment>,
    isLoading: Boolean,
    error: String?,
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onDismiss: () -> Unit,
    isPaging: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    event: Flow<PostCommentEvent>,
    onCommentAddedFailed: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val density = LocalDensity.current

    // Actual height of the input area in pixels.
    var composerHeightPx by remember {
        mutableIntStateOf(0)
    }

    val composerHeightDp = with(density) {
        composerHeightPx.toDp()
    }

    val commentsListState = rememberLazyListState()

    val currentIsPaging by rememberUpdatedState(isPaging)
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)

    LaunchedEffect(
        commentsListState,
        comments.size,
        optimisticComments.size,
        hasMore,
        isLoading
    ) {
        snapshotFlow {
            val layoutInfo = commentsListState.layoutInfo

            val lastVisibleItem = layoutInfo.visibleItemsInfo
                .lastOrNull()?.index ?: -1

            val totalItems = layoutInfo.totalItemsCount

            totalItems > 0 &&
                    lastVisibleItem >= (totalItems - 3).coerceAtLeast(0)
        }
            .distinctUntilChanged()
            .collect { isNearEnd ->

                if (
                    isNearEnd &&
                    hasMore &&
                    !isLoading &&
                    !currentIsPaging &&
                    (comments.isNotEmpty() || optimisticComments.isNotEmpty())
                ) {
                    currentOnLoadMore()
                }
            }
    }

    LaunchedEffect(true) {
        event.collect { event ->
            when (event) {
                PostCommentEvent.OnCommentAdded -> {
                    commentsListState.animateScrollToItem(0)
                }

                is PostCommentEvent.ShowError -> onCommentAddedFailed(event.err)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {

            // Maximum available screen height.
            val viewportHeightPx = with(density) {
                maxHeight.toPx()
            }

            // Keep the sheet content at 80% of available height.
            val sheetContentHeight = maxHeight * 0.8f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sheetContentHeight)
            ) {

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    // Header
                    Text(
                        text = "Comments",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 12.dp
                        )
                    )

                    HorizontalDivider()

                    // Scrollable comments area.
                    when {

                        isLoading -> {
                            CommentShimmer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }

                        error != null -> {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        comments.isEmpty() && optimisticComments.isEmpty() -> {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    text = "No comments yet. Be the first!",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        else -> {

                            LazyColumn(
                                state = commentsListState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(
                                        bottom = composerHeightDp
                                    ),
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 16.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {

                                if (optimisticComments.isNotEmpty()) {
                                    items(optimisticComments, key = { it.id }) { optComment ->
                                        CommentItem(
                                            optComment.toRealComment()
                                        )
                                    }
                                }
                                items(
                                    items = comments,
                                    key = { it.id }
                                ) { comment ->

                                    CommentItem(comment)
                                }

                                // Pagination loading indicator
                                if (isPaging) {
                                    item(key = "comments_paging") {
                                        CommentPagingShimmer()
                                    }
                                }
                            }
                        }
                    }
                }

                // Fixed comment input.
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .onSizeChanged { size ->

                            if (composerHeightPx != size.height) {
                                composerHeightPx = size.height
                            }
                        }
                        .offset {

                            // Current vertical position of the sheet.
                            val sheetOffset = sheetState.requireOffset()

                            // Position the composer at the visible bottom edge.
                            val desiredY =
                                viewportHeightPx -
                                        sheetOffset -
                                        composerHeightPx

                            val maxY = with(density) {
                                sheetContentHeight.toPx()
                            } - composerHeightPx

                            IntOffset(
                                x = 0,
                                y = desiredY
                                    .coerceIn(0f, maxY.coerceAtLeast(0f))
                                    .roundToInt()
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(
                                horizontal = 12.dp,
                                vertical = 10.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        val canSend =
                            commentText.isNotBlank()

                        TextFieldComponent(
                            modifier = Modifier.weight(1f),
                            value = commentText,
                            label = "Add a comment...",
                            onValueChange = onCommentTextChange,
                            singleLine = false,
                            minLines = 1,
                            maxLines = 4,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (canSend) {
                                        onSendComment()
                                    }
                                }
                            ),
                            trailingIcon = {

                                IconButton(
                                    onClick = onSendComment,
                                    enabled = canSend
                                ) {

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send comment",
                                        tint = if (canSend) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: Comment
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        // Profile picture or fallback avatar.
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (!comment.profileImageUrl.isNullOrBlank()) {

                AsyncImage(
                    model = comment.profileImageUrl,
                    contentDescription = "${comment.authorName}'s profile picture",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentScale = ContentScale.Crop
                )

            } else {

                Text(
                    text = comment.authorName
                        .firstOrNull()
                        ?.uppercase() ?: "?",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Author name and comment.
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = comment.updatedAt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CommentPagingShimmer() {
    val transition = rememberInfiniteTransition(
        label = "comment_paging_shimmer"
    )

    val offset by transition.animateFloat(
        initialValue = -300f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "shimmer_offset"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        start = Offset(offset, 0f),
        end = Offset(offset + 200f, 100f)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(brush)
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth(0.35f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
            )
        }
    }
}