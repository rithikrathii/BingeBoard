package com.example.bingeboard.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.User
import com.example.bingeboard.data.repository.AuthRepository
import com.example.bingeboard.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HomeUiState(
    val movies: List<Movie> = emptyList(),
    val filteredMovies: List<Movie> = emptyList(),
    val genres: List<String> = emptyList(),
    val selectedGenre: String = "All",
    val searchQuery: String = "",
    val currentUser: User? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val movies = movieRepository.getAllMovies()
        val genres = movieRepository.getGenres()
        val user = authRepository.getCurrentUser()
        _uiState.update { 
            it.copy(
                movies = movies, 
                filteredMovies = movies,
                genres = genres,
                currentUser = user
            ) 
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
        val currentState = _uiState.value
        val filtered = currentState.movies.filter { movie ->
            val matchesGenre = currentState.selectedGenre == "All" || movie.genre.contains(currentState.selectedGenre)
            val matchesSearch = movie.title.contains(currentState.searchQuery, ignoreCase = true) ||
                    movie.genre.any { it.contains(currentState.searchQuery, ignoreCase = true) }
            matchesGenre && matchesSearch
        }
        _uiState.update { it.copy(filteredMovies = filtered) }
    }
}
