package com.iftikar.outlier.core.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.auth.api.LoginNavKey
import com.iftikar.outlier.feature.home.api.HomeNavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _startDestination = MutableStateFlow<NavKey?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        getRoute()
    }

    private fun getRoute() {
        viewModelScope.launch {
            try {
                val refreshToken = sessionManager.getRefreshToken()

                _startDestination.value = if (!refreshToken.isNullOrBlank()) {
                    HomeNavKey
                } else {
                    LoginNavKey
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                _startDestination.value = LoginNavKey
            }
        }
    }
}