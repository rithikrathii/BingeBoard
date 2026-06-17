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

// Holds everything the home screen needs to draw itself at any moment.
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
    val hasMoreMovies: Boolean = true,
    val isBrowsingAll: Boolean = true
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
        loadGenresAndUser()
        loadFirstPage()
    }

    private fun loadGenresAndUser() {
        viewModelScope.launch {
            val genres = listOf("All") + movieRepository.getGenres()
            val user = authRepository.getCurrentUser()
            _uiState.update { it.copy(genres = genres, currentUser = user) }
        }
    }

    // Loads ONLY the first page of movies. This should be fast on startup.
    private fun loadFirstPage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val firstPage = movieRepository.getMoviesPage(page = 1, limit = 50)
            println("First page loaded: ${firstPage.size} movies")
            _uiState.update {
                it.copy(
                    movies = firstPage,
                    displayedMovies = firstPage.take(20),
                    isLoading = false,
                    currentPage = 1,
                    hasMoreMovies = true,
                    isBrowsingAll = true
                )
            }
        }
    }

    // Called by the UI when the user scrolls near the bottom.
    fun loadMore() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasMoreMovies) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            if (currentState.isBrowsingAll) {
                // TRUE lazy loading: only now do we ask the network for the
                // next page, exactly when the user scrolled and needs it.
                val nextApiPage = (currentState.movies.size / 50) + 1
                println("loadMore: fetching API page $nextApiPage (browse mode)")
                val newPage = movieRepository.getMoviesPage(page = nextApiPage, limit = 50)
                println("loadMore: got ${newPage.size} new movies, total now ${currentState.movies.size + newPage.size}")
                val combined = currentState.movies + newPage
                _uiState.update {
                    it.copy(
                        movies = combined,
                        displayedMovies = combined.take(it.displayedMovies.size + it.pageSize),
                        isLoadingMore = false,
                        hasMoreMovies = newPage.isNotEmpty()
                    )
                }
            } else {
                // Genre/search/filter mode: reveal more of the already-loaded set.
                val nextPage = currentState.currentPage + 1
                val endIndex = minOf(nextPage * currentState.pageSize, allFilteredMovies.size)
                val newMovies = allFilteredMovies.take(endIndex)
                println("loadMore: revealing more from filtered set, now showing ${newMovies.size} of ${allFilteredMovies.size}")
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
    }

    fun onGenreSelected(genre: String) {
        _uiState.update { it.copy(selectedGenre = genre) }
        if (genre == "All") {
            loadFirstPage()
        } else {
            applyFiltersWithGenre(genre)
        }
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
            loadFirstPage()
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
                    hasMoreMovies = results.size > 20,
                    isBrowsingAll = false
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
        val currentState = _uiState.value
        applyFiltersWithGenre(currentState.selectedGenre)
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                selectedGenre = "All",
                yearMin = "",
                yearMax = "",
                selectedRated = "",
                selectedLanguage = "",
                showFilterSheet = false,
                searchQuery = ""
            )
        }
        loadFirstPage()
    }

    private fun applyFiltersWithGenre(genre: String) {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val filtered = when {
                genre != "All" ||
                        currentState.yearMin.isNotEmpty() ||
                        currentState.yearMax.isNotEmpty() ||
                        currentState.selectedRated.isNotEmpty() ||
                        currentState.selectedLanguage.isNotEmpty() -> {
                    movieRepository.filterMovies(
                        genre = if (genre == "All") null else genre,
                        yearMin = currentState.yearMin.toIntOrNull(),
                        yearMax = currentState.yearMax.toIntOrNull(),
                        rated = currentState.selectedRated.ifEmpty { null },
                        language = currentState.selectedLanguage.ifEmpty { null }
                    )
                }
                else -> currentState.movies
            }
            println("Filter results for genre '$genre': ${filtered.size}")
            allFilteredMovies = filtered
            _uiState.update {
                it.copy(
                    displayedMovies = filtered.take(20),
                    isLoading = false,
                    currentPage = 1,
                    hasMoreMovies = filtered.size > 20,
                    isBrowsingAll = false
                )
            }
        }
    }
}