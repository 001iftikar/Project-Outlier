package com.iftikar.outlier.feature.auth.impl

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iftikar.outlier.core.designsystem.component.checkbox.CheckboxComponent
import com.iftikar.outlier.core.designsystem.component.input.TextFieldComponent
import com.iftikar.outlier.feature.auth.component.GradientBackground

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onSuccess: (String, String, String) -> Unit, // username, email, role
    onAlreadyUserClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action = viewModel::onAction
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when(event) {
                is RegisterScreenEvent.OnSuccess -> onSuccess(event.username, event.email, event.role.name)
                is RegisterScreenEvent.OnError -> {
                    Toast
                        .makeText(context, event.error, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        GradientBackground(
            modifier = Modifier.padding(innerPadding)
        ) {
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                ) {
                    TextFieldComponent(
                        value = state.username,
                        label = "Username (required)",
                        onValueChange = { action(RegisterScreenAction.OnUsernameChange(it)) },
                        supportingText = state.usernameError
                    )
                    TextFieldComponent(
                        value = state.email,
                        label = "Email (required)",
                        onValueChange = { action(RegisterScreenAction.OnEmailChange(it)) },
                        supportingText = state.emailError
                    )
                    TextFieldComponent(
                        value = state.password,
                        label = "Password (required)",
                        onValueChange = { action(RegisterScreenAction.OnPasswordChange(it)) },
                        supportingText = state.passwordError,
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(
                                onClick = {action(RegisterScreenAction.OnPasswordEyeClick)}
                            ) {
                                Icon(
                                    imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            CheckboxComponent(
                                text = "I am a developer ${state.isRoleRequiredText}",
                                checked = state.isDevChecked,
                                onCheckedChange = { action(RegisterScreenAction.OnIsDevChecked(it)) }
                            )
                            Spacer(Modifier.height(4.dp))
                            CheckboxComponent(
                                text = "I am a recruiter ${state.isRoleRequiredText}",
                                checked = state.isRecruiterChecked,
                                onCheckedChange = { action(RegisterScreenAction.OnIsRecruiterChecked(it)) }
                            )
                        }
                    }
                    Button(
                        onClick = {action(RegisterScreenAction.OnRegisterClick)},
                        enabled = state.isRegisterButtonEnabled
                    ) {
                        Text(
                            text = state.buttonText
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
        }
    }

}
















