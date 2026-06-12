package com.example.bingeboard.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.User
import com.example.bingeboard.data.repository.AuthRepository
import com.example.bingeboard.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val movies: List<Movie> = emptyList(),
    val filteredMovies: List<Movie> = emptyList(),
    val genres: List<String> = emptyList(),
    val selectedGenre: String = "All",
    val searchQuery: String = "",
    val currentUser: User? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val movies = movieRepository.getAllMovies()
            val genres = listOf("All") + movieRepository.getGenres()
            val user = authRepository.getCurrentUser()
            _uiState.update {
                it.copy(
                    movies = movies,
                    filteredMovies = movies,
                    genres = genres,
                    currentUser = user,
                    isLoading = false
                )
            }
        }
    }

    fun onGenreSelected(genre: String) {
        _uiState.update { it.copy(selectedGenre = genre) }
        filterMovies()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterMovies()
    }

    private fun filterMovies() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isLoading = true) }

            val filtered = if (currentState.selectedGenre == "All" && currentState.searchQuery.isEmpty()) {
                currentState.movies
            } else {
                movieRepository.getAllMovies().filter { movie ->
                    val matchesGenre = currentState.selectedGenre == "All" || movie.genres.contains(currentState.selectedGenre)
                    val matchesSearch = movie.title.contains(currentState.searchQuery, ignoreCase = true) ||
                            movie.genres.any { it.contains(currentState.searchQuery, ignoreCase = true) }
                    matchesGenre && matchesSearch
                }
            }
            _uiState.update { it.copy(filteredMovies = filtered, isLoading = false) }
        }
    }
}