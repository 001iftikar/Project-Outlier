package com.iftikar.outlier.feature.auth.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iftikar.outlier.feature.auth.component.EnterCodeComponent
import com.iftikar.outlier.feature.auth.component.EnterEmailComponent
import com.iftikar.outlier.feature.auth.component.GradientBackground

@Composable
fun EmailVerificationScreen(
    viewModel: EmailVerificationViewModel,
    onAlreadyUserClick: () -> Unit,
    onSuccess: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action = viewModel::onAction

    LaunchedEffect(true) {
        viewModel.event.collect { event ->
            when(event) {
                is EmailVerificationScreenEvent.OnSuccess -> onSuccess(event.email)
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        GradientBackground(modifier = Modifier.padding(innerPadding)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF18181B).copy(alpha = 0.4f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.showEmailScreen) {
                    EnterEmailComponent(
                        email = state.email,
                        onEmailChange = { action(EmailVerificationScreenAction.OnEmailChange(it)) },
                        emailError = state.emailError,
                        buttonEnabled = state.isEmailValid,
                        onButtonClicked = { action(EmailVerificationScreenAction.OnSendOtpClick) },
                        isLoading = state.isSendingOtp,
                        sendingError = state.sendOtpError,
                        onAlreadyUserClick = onAlreadyUserClick
                    )
                } else {
                    EnterCodeComponent(
                        code = state.code,
                        onCodeChange = { action(EmailVerificationScreenAction.OnCodeChange(it)) },
                        buttonEnabled = state.code.isNotEmpty(),
                        onButtonClicked = { action(EmailVerificationScreenAction.OnVerifyOtpClick) },
                        isLoading = state.isVerifyingOtp,
                        verifyingError = state.verifyOtpError,
                        maskedEmail = state.maskedEmail,
                        onChangeEmailClick = {action(EmailVerificationScreenAction.OnChangeEmailClick)}
                    )
                }
            }
        }
    }
}