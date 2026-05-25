package com.example.cinerate.ui.screens.about

import androidx.lifecycle.ViewModel
import com.example.cinerate.data.model.User
import com.example.cinerate.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AboutUiState(
    val currentUser: User? = null
)

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = AboutUiState(currentUser = authRepository.getCurrentUser())
    }
}
