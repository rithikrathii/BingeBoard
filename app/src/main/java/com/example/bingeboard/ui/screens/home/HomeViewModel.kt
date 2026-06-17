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
// Compose watches this and redraws whenever it changes.
data class HomeUiState(
    val movies: List<Movie> = emptyList(),          // the full list of loaded movies
    val displayedMovies: List<Movie> = emptyList(), // the slice currently shown on screen
    val genres: List<String> = emptyList(),         // genre chips (starts with "All")
    val selectedGenre: String = "All",              // which genre chip is active
    val searchQuery: String = "",                   // current text in the search bar
    val currentUser: User? = null,                  // logged-in user, or null if guest
    val isLoading: Boolean = false,                 // full-screen loading spinner
    val isLoadingMore: Boolean = false,             // small spinner when scrolling for more
    val yearMin: String = "",                       // advanced filter: earliest year
    val yearMax: String = "",                       // advanced filter: latest year
    val selectedRated: String = "",                 // advanced filter: age rating
    val selectedLanguage: String = "",              // advanced filter: language
    val showFilterSheet: Boolean = false,           // is the filter bottom sheet open
    val currentPage: Int = 1,                       // current page for infinite scroll
    val pageSize: Int = 20,                          // how many movies to add per scroll
    val hasMoreMovies: Boolean = true               // are there more movies left to show
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Private mutable state we update, exposed as read-only to the UI.
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Keeps track of the running search so we can cancel it (used for debounce).
    private var searchJob: Job? = null

    // The full result set we are currently paging through (all movies,
    // or the filtered/searched subset). displayedMovies is a slice of this.
    private var allFilteredMovies: List<Movie> = emptyList()

    // Load movies, genres and the current user as soon as the screen opens.
    init {
        loadData()
    }

    // Fetches all movies, the genre list and the logged-in user from the repository.
    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val movies = movieRepository.getAllMovies()
            val genres = listOf("All") + movieRepository.getGenres() // "All" sits at the front
            val user = authRepository.getCurrentUser()
            allFilteredMovies = movies
            _uiState.update {
                it.copy(
                    movies = movies,
                    displayedMovies = movies.take(20), // only show first 20 to start
                    genres = genres,
                    currentUser = user,
                    isLoading = false,
                    hasMoreMovies = movies.size > 20
                )
            }
        }
    }

    // Called by the UI when the user scrolls near the bottom.
    // Reveals the next batch of 20 movies from the list we already have.
    fun loadMore() {
        val currentState = _uiState.value
        // Don't do anything if we're already loading or there's nothing left.
        if (currentState.isLoadingMore || !currentState.hasMoreMovies) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            delay(300) // tiny pause so the loading spinner is visible
            val nextPage = currentState.currentPage + 1
            // Take up to nextPage*20 items, but never more than we actually have.
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

    // Called when the user taps a genre chip.
    fun onGenreSelected(genre: String) {
        _uiState.update { it.copy(selectedGenre = genre) }
        if (genre == "All") {
            allFilteredMovies = _uiState.value.movies
            println("All movies count: ${_uiState.value.movies.size}")
            _uiState.update {
                it.copy(
                    displayedMovies = it.movies.take(20),
                    currentPage = 1,
                    hasMoreMovies = it.movies.size > 20,
                    isLoading = false
                )
            }
        } else {
            // Any other genre goes through the API filter.
            applyFiltersWithGenre(genre)
        }
    }

    // Called on every keystroke in the search bar.
    // We use a 500ms debounce: cancel the previous pending search and start
    // a new one, so the API is only hit once the user pauses typing.
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            performSearch(query)
        }
    }

    // Actually performs the search once the debounce delay has passed.
    private fun performSearch(query: String) {
        // If the search box was cleared, go back to showing all movies.
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

    // The next four functions just store what the user types/picks in the
    // filter sheet. The actual filtering happens when they tap "Apply".
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

    // Opens or closes the advanced filter bottom sheet.
    fun toggleFilterSheet() {
        _uiState.update { it.copy(showFilterSheet = !it.showFilterSheet) }
    }

    // Called when the user taps "Apply" in the filter sheet.
    // Closes the sheet and runs the filter with whatever is currently selected.
    fun applyAdvancedFilter() {
        _uiState.update { it.copy(showFilterSheet = false) }
        val currentState = _uiState.value
        applyFiltersWithGenre(currentState.selectedGenre)
    }

    // Resets everything back to the default "show all movies" state.
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

    // Core filtering logic. We pass the genre in directly as a parameter
    // (instead of reading it from state) because the state update from
    // onGenreSelected might not have applied yet - this avoids a stale-state bug.
    private fun applyFiltersWithGenre(genre: String) {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val filtered = when {
                // If any filter is active, ask the backend for matching movies.
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
                // Otherwise nothing is filtered, so just show all movies.
                else -> currentState.movies
            }
            println("Filter results for genre '$genre': ${filtered.size}")
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