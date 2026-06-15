package com.example.bingeboard.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.User
import com.example.bingeboard.data.repository.AuthRepository
import com.example.bingeboard.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val movies: List<Movie> = emptyList(),
    val displayedMovies: List<Movie> = emptyList(),
    val genres: List<String> = emptyList(),
    val selectedGenre: String = "All",
    val searchQuery: String = "",
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val yearMin: String = "",
    val yearMax: String = "",
    val selectedRated: String = "",
    val selectedLanguage: String = "",
    val showFilterSheet: Boolean = false,
    val currentPage: Int = 1,
    val pageSize: Int = 20,
    val hasMoreMovies: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var searchJob: Job? = null
    private var allFilteredMovies: List<Movie> = emptyList()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val movies = movieRepository.getAllMovies()
            val genres = listOf("All") + movieRepository.getGenres()
            val user = authRepository.getCurrentUser()
            allFilteredMovies = movies
            println("Loaded ${movies.size} movies")
            _uiState.update {
                it.copy(
                    movies = movies,
                    displayedMovies = movies.take(20),
                    genres = genres,
                    currentUser = user,
                    isLoading = false,
                    hasMoreMovies = movies.size > 20
                )
            }
        }
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasMoreMovies) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            delay(300)
            val nextPage = currentState.currentPage + 1
            val endIndex = minOf(nextPage * currentState.pageSize, allFilteredMovies.size)
            val newMovies = allFilteredMovies.take(endIndex)
            _uiState.update {
                it.copy(
                    displayedMovies = newMovies,
                    currentPage = nextPage,
                    isLoadingMore = false,
                    hasMoreMovies = endIndex < allFilteredMovies.size
                )
            }
        }
    }

    fun onGenreSelected(genre: String) {
        _uiState.update { it.copy(selectedGenre = genre) }
        applyFilters()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            allFilteredMovies = _uiState.value.movies
            _uiState.update {
                it.copy(
                    displayedMovies = it.movies.take(20),
                    isLoading = false,
                    currentPage = 1,
                    hasMoreMovies = it.movies.size > 20
                )
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val results = movieRepository.searchMovies(query)
            println("Search results for '$query': ${results.size}")
            allFilteredMovies = results
            _uiState.update {
                it.copy(
                    displayedMovies = results.take(20),
                    isLoading = false,
                    currentPage = 1,
                    hasMoreMovies = results.size > 20
                )
            }
        }
    }

    fun onYearMinChanged(year: String) {
        _uiState.update { it.copy(yearMin = year) }
    }

    fun onYearMaxChanged(year: String) {
        _uiState.update { it.copy(yearMax = year) }
    }

    fun onRatedChanged(rated: String) {
        _uiState.update { it.copy(selectedRated = rated) }
    }

    fun onLanguageChanged(language: String) {
        _uiState.update { it.copy(selectedLanguage = language) }
    }

    fun toggleFilterSheet() {
        _uiState.update { it.copy(showFilterSheet = !it.showFilterSheet) }
    }

    fun applyAdvancedFilter() {
        _uiState.update { it.copy(showFilterSheet = false) }
        applyFilters()
    }

    fun clearFilters() {
        allFilteredMovies = _uiState.value.movies
        _uiState.update {
            it.copy(
                selectedGenre = "All",
                yearMin = "",
                yearMax = "",
                selectedRated = "",
                selectedLanguage = "",
                showFilterSheet = false,
                searchQuery = "",
                displayedMovies = it.movies.take(20),
                currentPage = 1,
                hasMoreMovies = it.movies.size > 20
            )
        }
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val filtered = when {
                currentState.selectedGenre != "All" ||
                        currentState.yearMin.isNotEmpty() ||
                        currentState.yearMax.isNotEmpty() ||
                        currentState.selectedRated.isNotEmpty() ||
                        currentState.selectedLanguage.isNotEmpty() -> {
                    movieRepository.filterMovies(
                        genre = if (currentState.selectedGenre == "All") null else currentState.selectedGenre,
                        yearMin = currentState.yearMin.toIntOrNull(),
                        yearMax = currentState.yearMax.toIntOrNull(),
                        rated = currentState.selectedRated.ifEmpty { null },
                        language = currentState.selectedLanguage.ifEmpty { null }
                    )
                }
                else -> currentState.movies
            }

            allFilteredMovies = filtered
            _uiState.update {
                it.copy(
                    displayedMovies = filtered.take(20),
                    isLoading = false,
                    currentPage = 1,
                    hasMoreMovies = filtered.size > 20
                )
            }
        }
    }
}