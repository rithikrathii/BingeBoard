package com.example.bingeboard.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.Review
import com.example.bingeboard.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val movie: Movie? = null,
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val reviewText: String = "",
    val reviewRating: Int = 5,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: String = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetail()
    }

    private fun loadMovieDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val movie = repository.getMovieById(movieId)
            val reviews = repository.getReviewsForMovie(movieId)
            _uiState.value = DetailUiState(movie = movie, reviews = reviews, isLoading = false)
        }
    }

    fun onReviewTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(reviewText = text)
    }

    fun onRatingChanged(rating: Int) {
        _uiState.value = _uiState.value.copy(reviewRating = rating)
    }

    fun submitReview(reviewerName: String) {
        val currentState = _uiState.value
        if (currentState.reviewText.isBlank()) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSubmitting = true)
            val review = Review(
                id = System.currentTimeMillis().toString(),
                movieId = movieId,
                reviewerName = reviewerName,
                reviewerInitials = reviewerName.split(" ")
                    .mapNotNull { it.firstOrNull()?.toString() }
                    .take(2)
                    .joinToString(""),
                date = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date()),
                rating = currentState.reviewRating,
                comment = currentState.reviewText
            )
            val success = repository.addReview(movieId, review)
            if (success) {
                _uiState.value = _uiState.value.copy(
                    reviews = _uiState.value.reviews + review,
                    reviewText = "",
                    reviewRating = 5,
                    isSubmitting = false,
                    submitSuccess = true
                )
            } else {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
            }
        }
    }
}