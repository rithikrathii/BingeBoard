package com.example.bingeboard.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.Review
import com.example.bingeboard.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class DetailUiState(
    val movie: Movie? = null,
    val reviews: List<Review> = emptyList()
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        val movie = repository.getMovieById(movieId)
        val reviews = repository.getReviewsForMovie(movieId)
        _uiState.value = DetailUiState(movie = movie, reviews = reviews)
    }
}
