package com.iftikar.outlier.feature.post.impl

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(PostScreenState())
    val state = _state.asStateFlow()

    fun onAction(action: PostScreenAction) {
        when(action) {
            is PostScreenAction.OnTitleChange -> _state.update { it.copy(title = action.title) }
            is PostScreenAction.OnDescChange -> _state.update { it.copy(description = action.desc) }
            is PostScreenAction.OnImageSelected -> _state.update { it.copy(selectedImages = action.images) }
            PostScreenAction.OnNextImage -> _state.update { it.copy(currentImageIndex = it.currentImageIndex + 1) }
            PostScreenAction.OnPrevImage -> _state.update { it.copy(currentImageIndex = it.currentImageIndex - 1) }
            PostScreenAction.OnFullScreenDismiss -> _state.update { it.copy(showImageInFullScreen = false) }
            PostScreenAction.OnShowFullScreen -> _state.update { it.copy(showImageInFullScreen = true) }
            is PostScreenAction.OnPictureRemove -> {
                val currentImages = _state.value.selectedImages
                if (action.index in currentImages.indices) {
                    val updatedList = currentImages.toMutableList()
                    updatedList.removeAt(action.index)

                    _state.update { currentState ->
                        val shouldShowFullScreen = updatedList.isNotEmpty() && currentState.showImageInFullScreen
                        val nextIndex = if (currentState.currentImageIndex >= updatedList.size && updatedList.isNotEmpty()) {
                            updatedList.size - 1
                        } else {
                            maxOf(0, currentState.currentImageIndex)
                        }

                        currentState.copy(
                            selectedImages = updatedList.toList(),
                            currentImageIndex = nextIndex,
                            showImageInFullScreen = shouldShowFullScreen
                        )
                    }
                }
            }
        }
    }
}