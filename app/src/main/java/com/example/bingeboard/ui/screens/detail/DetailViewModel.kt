package com.example.bingeboard.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.Review
import com.example.bingeboard.data.repository.AuthRepository
import com.example.bingeboard.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Holds everything the movie detail screen needs: the movie itself,
// its reviews, and the state of the review-writing form.
data class DetailUiState(
    val movie: Movie? = null,
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val reviewText: String = "",          // what the user is typing in the review box
    val reviewRating: Int = 5,            // selected star rating (defaults to 5)
    val isSubmitting: Boolean = false,    // true while a review is being posted
    val submitSuccess: Boolean = false,   // true once a review posts successfully
    val currentUserName: String = "",     // logged-in user's name (for the review)
    val currentUserEmail: String = ""     // logged-in user's email (used as their ID)
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // The movie ID is passed in through navigation when this screen opens.
    private val movieId: String = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    // Load the movie and its reviews as soon as the screen opens.
    init {
        loadMovieDetail()
    }

    // Fetches the movie details, its reviews, and the current user.
    // We need the user so we know whose name to attach to new reviews
    // and which reviews show a delete button.
    private fun loadMovieDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val movie = repository.getMovieById(movieId)
            val reviews = repository.getReviewsForMovie(movieId)
            val user = authRepository.getCurrentUser()
            _uiState.value = DetailUiState(
                movie = movie,
                reviews = reviews,
                isLoading = false,
                currentUserName = user?.fullName ?: "Anonymous",
                currentUserEmail = user?.email ?: ""  // empty means not logged in
            )
        }
    }

    // Updates the review text as the user types.
    fun onReviewTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(reviewText = text)
    }

    // Updates the star rating when the user taps a star.
    fun onRatingChanged(rating: Int) {
        _uiState.value = _uiState.value.copy(reviewRating = rating)
    }

    // Posts the user's review to the backend.
    fun submitReview() {
        val currentState = _uiState.value
        if (currentState.reviewText.isBlank()) return  // don't submit empty reviews

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSubmitting = true)
            // Build the review object from the form + the logged-in user's info.
            val review = Review(
                id = System.currentTimeMillis().toString(),
                movieId = movieId,
                userId = currentState.currentUserEmail,
                reviewerName = currentState.currentUserName,
                // Initials from the name, e.g. "John Doe" -> "JD"
                reviewerInitials = currentState.currentUserName.split(" ")
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
                // Reload reviews from the server so we show the real saved data
                // (with the correct server ID/date) rather than guessing locally.
                val updatedReviews = repository.getReviewsForMovie(movieId)
                _uiState.value = _uiState.value.copy(
                    reviews = updatedReviews,
                    reviewText = "",        // clear the form
                    reviewRating = 5,       // reset stars back to 5
                    isSubmitting = false,
                    submitSuccess = true
                )
            } else {
                // Submission failed (e.g. not logged in) - just stop the spinner.
                _uiState.value = _uiState.value.copy(isSubmitting = false)
            }
        }
    }

    // Deletes a review and removes it from the list shown on screen.
    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            val success = repository.deleteReview(reviewId)
            if (success) {
                // Remove it locally so the UI updates instantly.
                _uiState.value = _uiState.value.copy(
                    reviews = _uiState.value.reviews.filter { it.id != reviewId }
                )
            }
        }
    }
}