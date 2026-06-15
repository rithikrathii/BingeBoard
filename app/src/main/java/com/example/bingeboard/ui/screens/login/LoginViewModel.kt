package com.example.bingeboard.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bingeboard.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClicked() {
        val currentState = _uiState.value

        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your email address") }
            return
        }

        if (!currentState.email.contains("@")) {
            _uiState.update { it.copy(error = "Please enter a valid email address") }
            return
        }

        if (currentState.password.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your password") }
            return
        }

        if (currentState.password.length < 8) {
            _uiState.update { it.copy(error = "Password must be at least 8 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = authRepository.login(currentState.email, currentState.password)

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: ""
                val displayError = when {
                    errorMessage.contains("401") -> "Incorrect email or password. Please try again."
                    errorMessage.contains("404") -> "No account found with this email. Please sign up."
                    errorMessage.contains("422") -> "Invalid email or password format."
                    else -> "Incorrect email or password. Please try again."
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = displayError
                    )
                }
            }
        }
    }
}