package com.iftikar.outlier.core.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.core.domain.repository.AuthRepository
import com.iftikar.outlier.feature.auth.api.LoginNavKey
import com.iftikar.outlier.feature.home.api.HomeNavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _startDestination = MutableStateFlow<NavKey?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        getRoute()
    }

    private fun getRoute() {
        viewModelScope.launch {
            val expiryString = sessionManager.getSessionExpiry()

            if (expiryString == null) {
                _startDestination.value = LoginNavKey
                return@launch
            }

            try {
                val expiryDate = Instant.parse(expiryString)
                val now = Instant.now()
                if (now.isAfter(expiryDate)) {
                    sessionManager.clearSession()
                    _startDestination.value = LoginNavKey
                    return@launch
                }
            } catch (e: Exception) {
                _startDestination.value = LoginNavKey
                return@launch
            }

            _startDestination.value = HomeNavKey
        }
    }
}