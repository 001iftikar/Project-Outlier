package com.iftikar.outlier.feature.auth.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.domain.repository.UserProfileRepository
import com.iftikar.outlier.core.result.CreateUserError
import com.iftikar.outlier.core.result.onError
import com.iftikar.outlier.core.result.onSuccess
import com.iftikar.outlier.feature.auth.api.CreateUserNavKey
import com.iftikar.outlier.feature.auth.component.GradientBackground
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * This creates the user in database after successful registration
 */
@Composable
fun CreateUserScreen(
    viewModel: CreateUserViewModel, onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                CreateUserScreenEvent.OnSuccess -> onSuccess()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        GradientBackground(modifier = Modifier.padding(innerPadding)) {
            if (state.isLoading) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Setting up your ${viewModel.args.role.lowercase()} account",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            if (state.error != null) {
                Button(
                    onClick = { viewModel.createUser() }) {
                    Text("Try again")
                }
            }
        }
    }
}

@HiltViewModel(assistedFactory = CreateUserViewModel.Factory::class)
class CreateUserViewModel @AssistedInject constructor(
    private val userProfileRepository: UserProfileRepository, @Assisted val args: CreateUserNavKey
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(args: CreateUserNavKey): CreateUserViewModel
    }

    private val _state = MutableStateFlow(CreateUserScreenState())
    val state = _state.asStateFlow()
    private val _event = Channel<CreateUserScreenEvent>()
    val event = _event.receiveAsFlow()

    init {
        createUser()
    }

    fun createUser() {
        viewModelScope.launch {
            _state.update { it.copy(error = null, isLoading = true) }
            userProfileRepository.createUser(
                name = args.username, email = args.email, role = args.role
            ).onSuccess {
                delay(2000L.milliseconds)
                _state.update { it.copy(isLoading = false) }
                _event.send(CreateUserScreenEvent.OnSuccess)
            }.onError { ex ->
                when (ex) {
                    CreateUserError.NO_INTERNET -> setError("Please check your internet connection and try again")
                    CreateUserError.NOT_AUTHORIZED -> setError("Authentication failed")
                    CreateUserError.UNKNOWN -> setError("Oops! Something happened")
                }
            }
        }
    }

    private fun setError(error: String) {
        _state.update {
            it.copy(isLoading = false, error = error)
        }
    }
}

data class CreateUserScreenState(
    val isLoading: Boolean = true, val error: String? = null
)

sealed interface CreateUserScreenEvent {
    data object OnSuccess : CreateUserScreenEvent
}