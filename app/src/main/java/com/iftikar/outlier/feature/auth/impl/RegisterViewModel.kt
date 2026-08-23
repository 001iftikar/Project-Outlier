package com.iftikar.outlier.feature.auth.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.datastore.SessionManager
import com.iftikar.outlier.core.domain.repository.AuthRepository
import com.iftikar.outlier.core.result.AuthError
import com.iftikar.outlier.core.result.GenericError
import com.iftikar.outlier.core.result.onError
import com.iftikar.outlier.core.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterScreenState())
    val state = _state.asStateFlow()

    private val _event = Channel<RegisterScreenEvent>()
    val event = _event.receiveAsFlow()

    private var usernameCheckJob: Job? = null

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
            is RegisterScreenAction.OnNameChange -> _state.update { it.copy(name = action.name) }
            RegisterScreenAction.OnRegisterClick -> register()
            RegisterScreenAction.OnPasswordEyeClick -> _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            is RegisterScreenAction.OnEmailChange -> _state.update { it.copy(email = action.email.trim()) }
            is RegisterScreenAction.OnUsernameChange -> {
                usernameCheckJob?.cancel()
                _state.update { it.copy(username = action.username, checkingUsername = false, isUsernameAvailable = null) }
                if (_state.value.username.isNotEmpty()) {
                    usernameCheckJob = viewModelScope.launch {
                        delay(1.seconds)
                        checkUsernameAvailability()
                    }
                }
            }
        }
    }

    private suspend fun checkUsernameAvailability() {
        _state.update { it.copy(checkingUsername = true) }

        authRepository.checkIfUsernameExists(_state.value.username).onSuccess { exists ->
            _state.update {
                it.copy(checkingUsername = false, isUsernameAvailable = !exists)
            }
        }.onError { ex ->
            _state.update {
                it.copy(checkingUsername = false, isUsernameAvailable = null)
            }
            when(ex) {
                GenericError.UNKNOWN -> {
                    _event.send(RegisterScreenEvent.OnError("Username check is failed. If this error persists, please contact the developer."))
                }
                GenericError.NO_INTERNET -> {
                    _event.send(RegisterScreenEvent.OnError("Please check your internet connection!"))
                }
            }
        }

    }

    private fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.register(
                username = _state.value.username,
                email = _state.value.email,
                password = _state.value.password,
                name = _state.value.name,
                role = _state.value.role.name
            ).onSuccess { email ->
                _state.update { it.copy(isLoading = false) }
                viewModelScope.launch {
                    _event.send(RegisterScreenEvent.OnSuccess(email))
                }
            }.onError { ex ->
                when (ex) {
                    AuthError.USER_EXISTS -> setError("User already exists with this email.")
                    AuthError.VALIDATION_ERROR -> setError("Oops! Something went wrong with the request.")
                    AuthError.AUTH_FAILED -> setError("Sending OTP failed.")
                    AuthError.TOO_MANY_REQUESTS -> setError("Too many requests, please try after sometime.")
                    AuthError.REQUEST_TIMEOUT -> setError("Request timeout, please try after sometime.")
                    AuthError.NO_INTERNET -> setError("Please check your internet connection and try again.")
                    AuthError.UNKNOWN -> setError("Oops! Something went wrong.")
                    else -> setError("Oops! Something went wrong.")
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

















