package com.iftikar.outlier.core.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.auth.api.LoginNavKey
import com.iftikar.outlier.feature.home.api.HomeNavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

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
            delay(2.seconds)
            try {
                val accessToken = sessionManager.getAccessToken()
                if (accessToken == null) {
                    _startDestination.value = LoginNavKey
                    return@launch
                }
                _startDestination.value = HomeNavKey
            } catch (e: Exception) {
                e.printStackTrace()
                _startDestination.value = LoginNavKey
                return@launch
            }
        }
    }
}