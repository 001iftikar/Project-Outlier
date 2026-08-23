package com.iftikar.outlier.feature.auth.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.datastore.SessionManager
import com.iftikar.outlier.core.domain.repository.AuthRepository
import com.iftikar.outlier.core.result.DescopeError
import com.iftikar.outlier.core.result.EmailVerificationError
import com.iftikar.outlier.core.result.onError
import com.iftikar.outlier.core.result.onSuccess
import com.iftikar.outlier.feature.auth.api.EmailVerifyNavKey
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
import javax.inject.Inject

@HiltViewModel(assistedFactory = EmailVerificationViewModel.Factory::class)
class EmailVerificationViewModel @AssistedInject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    @Assisted val args: EmailVerifyNavKey
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(args: EmailVerifyNavKey): EmailVerificationViewModel
    }
    private val _state = MutableStateFlow(EmailVerificationScreenState())
    val state = _state.asStateFlow()

    private val _event = Channel<EmailVerificationScreenEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: EmailVerificationScreenAction) {
        when (action) {
            is EmailVerificationScreenAction.OnCodeChange -> _state.update { it.copy(code = action.code) }
            EmailVerificationScreenAction.OnVerifyOtpClick -> verifyEmail()
        }
    }

    private fun verifyEmail() {
        viewModelScope.launch {
            _state.update {
                it.copy(isVerifyingOtp = true, verifyOtpError = null)
            }
            authRepository.verifyOtp(args.email, _state.value.code).onSuccess { session ->
                _state.update {
                    it.copy(isVerifyingOtp = false)
                }
                sessionManager.saveTokensOnFirstLogin(session)
                _event.send(EmailVerificationScreenEvent.OnSuccess)
            }.onError { ex ->
                _state.update {
                    it.copy(isVerifyingOtp = false)
                }
                when (ex) {
                    EmailVerificationError.VERIFICATION_NOT_FOUND -> showError("No pending email verification found.")
                    EmailVerificationError.OTP_ATTEMPTS_EXCEEDED -> showError("Too many incorrect attempts.")
                    EmailVerificationError.OTP_EXPIRED -> showError("OTP expired, please request a new one.")
                    EmailVerificationError.INVALID_OTP -> showError("You have entered a wrong OTP.")
                    EmailVerificationError.UNKNOWN -> showError("Oops! Something went wrong, OTP verification failed.")
                    EmailVerificationError.NO_INTERNET -> showError("Please check your internet connection.")
                }
            }
        }
    }

    private fun showError(error: String) {
        _state.update { it.copy(isVerifyingOtp = false) }
        viewModelScope.launch {
            _event.send(EmailVerificationScreenEvent.OnError(error))
        }
    }
}






















