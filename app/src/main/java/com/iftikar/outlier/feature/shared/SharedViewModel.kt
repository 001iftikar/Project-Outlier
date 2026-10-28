package com.iftikar.outlier.feature.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.domain.repository.UserProfileRepository
import com.iftikar.outlier.core.models.DrawerUserInfo
import com.iftikar.outlier.core.result.onError
import com.iftikar.outlier.core.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {
    private val _drawerUserInfoState = MutableStateFlow(DrawerUserInfoState())
    val drawerUserInfoState = _drawerUserInfoState.asStateFlow()

    var saveAction: (() -> Unit)? = null

    var shouldSavePostState: Boolean = false
        private set

    fun setShouldSavePostState(save: Boolean, action: (() -> Unit)? = null) {
        shouldSavePostState = save
        saveAction = action
    }

    fun triggerSave() {
        if (shouldSavePostState) {
            saveAction?.invoke() // Executes synchronously right now!
        }
    }

    init {
        fetchDrawerUserInfo()
    }

    fun fetchDrawerUserInfo() {
        _drawerUserInfoState.update { it.copy(isDrawerLoading = true) }
        viewModelScope.launch {
            userProfileRepository.getDrawerUserInfo().onSuccess { info ->
                _drawerUserInfoState.update {
                    it.copy(isDrawerLoading = false, drawerUserInfo = DrawerUserInfo(info.name, info.isDeveloper))
                }
            }.onError {
                _drawerUserInfoState.update { it.copy(isDrawerLoading = false) }
                // fail silently
            }
        }
    }
}





















