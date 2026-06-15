package com.example.bingeboard.ui.screens.signup

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

data class SignupUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onFullNameChanged(name: String) {
        _uiState.update { it.copy(fullName = name, error = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmPassword = password, error = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onSignupClicked() {
        val currentState = _uiState.value

        if (currentState.fullName.isBlank() || currentState.fullName.length < 2) {
            _uiState.update { it.copy(error = "Please enter your full name") }
            return
        }

        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your email address") }
            return
        }

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (!emailRegex.matches(currentState.email)) {
            _uiState.update { it.copy(error = "Please enter a valid email address") }
            return
        }

        if (currentState.password.isBlank() || currentState.password.length < 8) {
            _uiState.update { it.copy(error = "Password must be at least 8 characters") }
            return
        }

        if (currentState.password != currentState.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = authRepository.signup(
                currentState.fullName,
                currentState.email,
                currentState.password
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: ""
                val displayError = when {
                    errorMessage.contains("422") -> "This email is already registered. Please login instead."
                    errorMessage.contains("400") -> "This email is already registered. Please login instead."
                    errorMessage.contains("409") -> "This email is already registered. Please login instead."
                    errorMessage.contains("already") -> "This email is already registered. Please login instead."
                    else -> "Signup failed. Please try again."
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