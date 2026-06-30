package com.iftikar.outlier.feature.auth.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.domain.repository.AuthRepository
import com.iftikar.outlier.core.result.DescopeError
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
class EmailVerificationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(EmailVerificationScreenState())
    val state = _state.asStateFlow()

    private val _event = Channel<EmailVerificationScreenEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: EmailVerificationScreenAction) {
        when (action) {
            is EmailVerificationScreenAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            is EmailVerificationScreenAction.OnCodeChange -> _state.update { it.copy(code = action.code) }
            EmailVerificationScreenAction.OnSendOtpClick -> sendOtp()
            EmailVerificationScreenAction.OnVerifyOtpClick -> verifyEmail()
            EmailVerificationScreenAction.OnChangeEmailClick -> {
                _state.update {
                    it.copy(
                        code = "",
                        isVerifyingOtp = false,
                        verifyOtpError = null,
                        maskedEmail = null,
                        showEmailScreen = true
                    )
                }
            }
        }
    }

    private fun sendOtp() {
        viewModelScope.launch {
            _state.update {
                it.copy(isSendingOtp = true, sendOtpError = null)
            }
            authRepository.sendOtp(_state.value.email).onSuccess { maskedEmail ->
                _state.update {
                    it.copy(
                        maskedEmail = maskedEmail,
                        isSendingOtp = false,
                        showEmailScreen = false
                    )
                }
            }.onError { ex ->
                _state.update {
                    it.copy(isSendingOtp = true)
                }
                when (ex) {
                    DescopeError.NO_INTERNET -> {
                        _state.update { it.copy(sendOtpError = "You're offline, please connect to the internet and try again") }
                    }

                    else -> {
                        _state.update { it.copy(sendOtpError = "Oops! Something is wrong") }
                    }
                }
            }
        }
    }

    private fun verifyEmail() {
        viewModelScope.launch {
            _state.update {
                it.copy(isVerifyingOtp = true, verifyOtpError = null)
            }
            authRepository.verifyOtp(_state.value.email, _state.value.code).onSuccess { email ->
                _state.update {
                    it.copy(isVerifyingOtp = false)
                }
                _event.send(EmailVerificationScreenEvent.OnSuccess(email))
            }.onError { ex ->
                _state.update {
                    it.copy(isVerifyingOtp = true)
                }
                when (ex) {
                    DescopeError.NO_INTERNET -> {
                        _state.update { it.copy(verifyOtpError = "You're offline, please connect to the internet and try again") }
                    }

                    DescopeError.UNKNOWN -> {
                        _state.update { it.copy(verifyOtpError = "Oops! Something is wrong") }
                    }

                    DescopeError.WRONG_OTP -> {
                        _state.update { it.copy(verifyOtpError = "Wrong OTP") }
                    }
                }
            }
        }
    }
}






















