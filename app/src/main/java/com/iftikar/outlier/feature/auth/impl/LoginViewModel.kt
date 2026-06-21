package com.iftikar.outlier.feature.auth.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.datastore.SessionManager
import com.iftikar.outlier.core.domain.repository.AuthRepository
import com.iftikar.outlier.core.result.AuthError
import com.iftikar.outlier.core.result.onError
import com.iftikar.outlier.core.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _state = MutableStateFlow(LoginScreenState())
    val state = _state.asStateFlow()

    private val _event = Channel<LoginScreenEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: LoginScreenAction) {
        when(action) {
            is LoginScreenAction.OnEmailChange -> _state.update { it.copy(email = action.email.trim()) }
            LoginScreenAction.OnLoginClick -> login()
            is LoginScreenAction.OnPasswordChange -> _state.update { it.copy(password = action.password) }
            LoginScreenAction.OnPasswordEyeClick -> _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.login(
                email = _state.value.email,
                password = _state.value.password
            ).onSuccess { expire ->
                sessionManager.saveSessionExpiry(expire)
                _state.update { it.copy(isLoading = false) }
                _event.send(LoginScreenEvent.OnSuccess)
            }.onError { ex ->
                when(ex) {
                    AuthError.INVALID_EMAIL -> setError("Invalid email")
                    AuthError.AUTH_FAILED -> setError("Login failed! Please check your email or password")
                    AuthError.PASSWORD_MISMATCH -> setError("Invalid email or password")
                    AuthError.TOO_MANY_REQUESTS -> setError("Too many requests, please try after sometime")
                    AuthError.REQUEST_TIMEOUT -> setError("Request timeout, please try after sometime")
                    AuthError.NO_INTERNET -> setError("Please check your internet connection and try again")
                    else -> setError("Oops! Something happened")
                }
            }
        }
    }

    private fun setError(error: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = false, error = error)
            }
            _event.send(LoginScreenEvent.OnError(error))
        }
    }
}