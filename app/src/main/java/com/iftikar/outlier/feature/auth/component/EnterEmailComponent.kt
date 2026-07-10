package com.iftikar.outlier.feature.auth.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.core.designsystem.component.input.TextFieldComponent

@Composable
fun EnterEmailComponent(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String? = null,
    buttonEnabled: Boolean,
    onButtonClicked: () -> Unit,
    isLoading: Boolean,
    sendingError: String?,
    onAlreadyUserClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "First we need to verify your email"
        )
        TextFieldComponent(
            value = email,
            label = "Email (required)",
            onValueChange = { onEmailChange(it) },
            supportingText = emailError
        )
        if (!sendingError.isNullOrEmpty()) {
            Text(
                text = sendingError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(
            onClick = onButtonClicked,
            enabled = buttonEnabled
        ) {
            Text(
                text = if (isLoading) "Sending OTP" else "Send OTP"
            )
        }

        TextButton(
            onClick = onAlreadyUserClick
        ) {
            Text(
                text = "Already a user"
            )
        }
    }
}