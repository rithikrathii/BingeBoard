package com.example.bingeboard.data.repository

import com.example.bingeboard.data.local.TokenDataStore
import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.Review
import com.example.bingeboard.data.remote.api.MovieApiService
import com.example.bingeboard.data.remote.api.ReviewApiService
import com.example.bingeboard.data.remote.dto.ReviewRequest
import com.example.bingeboard.data.remote.mapper.toMovie
import javax.inject.Inject
import javax.inject.Singleton

// This is the real repository that talks to the backend services.
// It handles movies (catalogue service) and reviews (ratings service).
@Singleton
class ApiMovieRepository @Inject constructor(
    private val api: MovieApiService,        // movie catalogue endpoints (port 8001)
    private val reviewApi: ReviewApiService,  // reviews endpoints (port 8002)
    private val tokenDataStore: TokenDataStore // stores the JWT token for logged-in user
) : MovieRepository {

    // Kept for genre lookups and as a fallback for getMovieById.
    // NOT used for the home screen's initial load anymore - that
    // uses getMoviesPage() below for true lazy infinite scroll.
    override suspend fun getAllMovies(): List<Movie> {
        return try {
            val allMovies = mutableListOf<Movie>()
            for (page in 1..500) {
                val response = api.getMovies(page = page, limit = 50)
                if (response.isEmpty()) break
                allMovies.addAll(response.map { it.toMovie() })
            }
            allMovies
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Fetches exactly ONE page of movies from the backend - nothing more.
    // This is what makes infinite scroll truly lazy: the ViewModel calls
    // this only when the user scrolls near the bottom, so a network
    // request only happens when it's actually needed.
    override suspend fun getMoviesPage(page: Int, limit: Int): List<Movie> {
        return try {
            api.getMovies(page = page, limit = limit).mapNotNull {
                try { it.toMovie() } catch (e: Exception) { null }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Gets a single movie by its ID. If the direct call fails,
    // we fall back to searching through all movies as a backup.
    override suspend fun getMovieById(id: String): Movie? {
        return try {
            api.getMovieById(id).toMovie()
        } catch (e: Exception) {
            getAllMovies().find { it.id == id }
        }
    }

    // Fetches all reviews for a given movie and converts them into
    // our Review model that the UI understands.
    override suspend fun getReviewsForMovie(movieId: String): List<Review> {
        return try {
            reviewApi.getReviews(movieId).map { dto ->
                Review(
                    id = dto.id,
                    movieId = dto.movie_id,
                    userId = dto.user_id,
                    reviewerName = dto.user_name,
                    reviewerInitials = dto.user_name.split(" ")
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2)
                        .joinToString(""),
                    date = try {
                        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", java.util.Locale.getDefault())
                        val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                        val date = inputFormat.parse(dto.created_at)
                        outputFormat.format(date!!)
                    } catch (e: Exception) {
                        dto.created_at
                    },
                    rating = dto.rating,
                    comment = dto.text
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Gets the list of all unique genres, used to build the genre chips.
    override suspend fun getGenres(): List<String> {
        return try {
            api.getGenres()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Searches movies by a query string. Uses the backend's text search,
    // which matches whole words across title, plot and genres.
    override suspend fun searchMovies(query: String): List<Movie> {
        return try {
            api.searchMovies(query = query, page = 1, limit = 50).map { it.toMovie() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Loads movies for a single genre (up to 10 pages / 500 movies).
    override suspend fun getMoviesByGenre(genre: String): List<Movie> {
        return try {
            val allMovies = mutableListOf<Movie>()
            for (page in 1..10) {
                val response = api.filterMovies(genre = genre, page = page, limit = 50)
                if (response.isEmpty()) break
                allMovies.addAll(response.map { it.toMovie() })
            }
            allMovies
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Filters movies by any combination of genre, year range, rating and language.
    override suspend fun filterMovies(
        genre: String?,
        yearMin: Int?,
        yearMax: Int?,
        rated: String?,
        language: String?
    ): List<Movie> {
        val allMovies = mutableListOf<Movie>()
        for (page in 1..500) {
            val response = try {
                api.filterMovies(
                    genre = genre,
                    yearMin = yearMin,
                    yearMax = yearMax,
                    rated = rated,
                    language = language,
                    page = page,
                    limit = 50
                )
            } catch (e: Exception) {
                continue
            }
            if (response.isEmpty()) break
            allMovies.addAll(response.mapNotNull {
                try { it.toMovie() } catch (e: Exception) { null }
            })
        }
        return allMovies
    }

    override suspend fun addReview(movieId: String, review: Review): Boolean {
        return try {
            val token = tokenDataStore.getToken() ?: return false
            reviewApi.addReview(
                token = "Bearer $token",
                review = ReviewRequest(
                    movie_id = movieId,
                    text = review.comment,
                    user_name = review.reviewerName,
                    rating = review.rating
                )
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteReview(reviewId: String): Boolean {
        return try {
            val token = tokenDataStore.getToken() ?: return false
            reviewApi.deleteReview(
                token = "Bearer $token",
                reviewId = reviewId
            )
            true
        } catch (e: Exception) {
            false
        }
    }
}