package com.example.bingeboard.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bingeboard.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
        
        // Validation
        if (currentState.email.isBlank() || !currentState.email.contains("@")) {
            _uiState.update { it.copy(error = "Please enter a valid email address") }
            return
        }
        
        if (currentState.password.isBlank() || currentState.password.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Simulate network delay
            delay(1500)
            
            val result = authRepository.login(currentState.email, currentState.password)
            
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = result.exceptionOrNull()?.message ?: "Invalid email or password"
                    ) 
                }
            }
        }
    }
}
