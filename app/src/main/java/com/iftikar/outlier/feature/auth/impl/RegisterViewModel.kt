package com.iftikar.outlier.feature.auth.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.datastore.SessionManager
import com.iftikar.outlier.core.domain.repository.AuthRepository
import com.iftikar.outlier.core.result.AuthError
import com.iftikar.outlier.core.result.onError
import com.iftikar.outlier.core.result.onSuccess
import com.iftikar.outlier.feature.auth.api.RegisterNavKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = RegisterViewModel.Factory::class)
class RegisterViewModel @AssistedInject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    @Assisted val args: RegisterNavKey
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(args: RegisterNavKey): RegisterViewModel
    }
    private val _state = MutableStateFlow(RegisterScreenState())
    val state = _state.asStateFlow()

    private val _event = Channel<RegisterScreenEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: RegisterScreenAction) {
        when (action) {
            is RegisterScreenAction.OnIsDevChecked -> _state.update {
                it.copy(
                    isDevChecked = action.checked,
                    isRecruiterChecked = if (action.checked) false else it.isRecruiterChecked
                )
            }

            is RegisterScreenAction.OnIsRecruiterChecked -> _state.update {
                it.copy(
                    isRecruiterChecked = action.checked,
                    isDevChecked = if (action.checked) false else it.isDevChecked
                )
            }

            is RegisterScreenAction.OnPasswordChange -> _state.update { it.copy(password = action.password) }
            is RegisterScreenAction.OnNameChange -> _state.update { it.copy(name = action.username.trim()) }
            RegisterScreenAction.OnRegisterClick -> register()
            RegisterScreenAction.OnPasswordEyeClick -> _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
        }
    }

    private fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.register(
                email = args.email,
                password = _state.value.password
            ).onSuccess { session ->
                _state.update { it.copy(isLoading = false) }
                sessionManager.saveSession(userId = session.userId, expiry = session.expire)
                _event.send(
                    RegisterScreenEvent.OnSuccess(
                        username = _state.value.name,
                        email = args.email,
                        role = _state.value.role
                    )
                )
            }.onError { ex ->
                when (ex) {
                    AuthError.USER_EXISTS -> setError("User already exists with the same username or email")
                    AuthError.INVALID_EMAIL -> setError("Invalid email")
                    AuthError.AUTH_FAILED -> setError("Authentication failed")
                    AuthError.PASSWORD_MISMATCH -> setError("Invalid email or password")
                    AuthError.TOO_MANY_REQUESTS -> setError("Too many requests, please try after sometime")
                    AuthError.REQUEST_TIMEOUT -> setError("Request timeout, please try after sometime")
                    AuthError.NO_INTERNET -> setError("Please check your internet connection and try again")
                    AuthError.UNKNOWN -> setError("Oops! Something happened")
                }
            }
        }
    }

    private fun setError(error: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = false, error = error)
            }
            _event.send(RegisterScreenEvent.OnError(error))
        }
    }
}

















