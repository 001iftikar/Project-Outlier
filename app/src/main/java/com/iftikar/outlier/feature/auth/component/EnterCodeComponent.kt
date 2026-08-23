package com.iftikar.outlier.feature.auth.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.core.designsystem.component.input.TextFieldComponent
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun EnterCodeComponent(
    code: String,
    onCodeChange: (String) -> Unit,
    buttonEnabled: Boolean,
    onButtonClicked: () -> Unit,
    isLoading: Boolean,
    verifyingError: String?,
    email: String?,
    onBackToRegister: () -> Unit
) {
    var resendIn by retain { mutableIntStateOf(59) }
    LaunchedEffect(resendIn) {
        if (resendIn > 0) {
            delay(1000L.milliseconds)
            resendIn--
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    )
    {
        if (!email.isNullOrEmpty()) {

            Text(
                text = "An OTP has been sent to $email",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        TextFieldComponent(
            value = code,
            label = "Otp (required)",
            onValueChange = { onCodeChange(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        )
        if (!verifyingError.isNullOrEmpty()) {
            Text(
                text = verifyingError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(
            onClick = { onButtonClicked(); resendIn = 59 },
            enabled = resendIn == 0
        ) {
            Text(
                text = if (resendIn > 0) "Resend OTP in ${resendIn}s" else "Resend OTP"
            )
        }
        Button(
            onClick = onButtonClicked,
            enabled = buttonEnabled
        ) {
            Text(
                text = if (isLoading) "Verifying" else "Verify"
            )
        }

        TextButton(
            onClick = onBackToRegister
        ) {
            Text(
                text = "Back to register",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}