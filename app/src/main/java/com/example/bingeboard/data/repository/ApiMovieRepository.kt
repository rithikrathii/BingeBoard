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

    // Loads every movie from the catalogue on app startup.
    // The backend only returns 50 movies per request, so we keep
    // requesting the next page until we get an empty response.
    override suspend fun getAllMovies(): List<Movie> {
        return try {
            val allMovies = mutableListOf<Movie>()
            for (page in 1..500) {
                val response = api.getMovies(page = page, limit = 50)
                if (response.isEmpty()) break  // no more movies left, stop looping
                allMovies.addAll(response.map { it.toMovie() })
            }
            println("Loaded ${allMovies.size} movies")
            allMovies
        } catch (e: Exception) {
            emptyList()  // return nothing if the network call fails
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
                    // Build initials from the reviewer's name (e.g. "John Doe" -> "JD")
                    reviewerInitials = dto.user_name.split(" ")
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2)
                        .joinToString(""),
                    // The backend sends an ISO timestamp, so we reformat it
                    // into something friendly like "Jun 17, 2026".
                    // If parsing fails for any reason, we just show the raw date.
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
    // Kept for backwards compatibility - the main filtering now goes
    // through filterMovies() below.
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
    // Because the backend caps each request at 50 results, we loop through pages
    // to collect the full list (e.g. all 12,000+ Drama movies).
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
                continue  // skip this page if it fails and keep going
            }
            if (response.isEmpty()) break  // reached the end of the results
            // mapNotNull skips any movie that fails to parse (e.g. a corrupted
            // year like "1994è1998") instead of crashing the whole page.
            allMovies.addAll(response.mapNotNull {
                try { it.toMovie() } catch (e: Exception) { null }
            })
        }
        return allMovies
    }

    // Submits a new review for a movie. Requires the user to be logged in,
    // since the backend needs the JWT token to know who is posting.
    override suspend fun addReview(movieId: String, review: Review): Boolean {
        return try {
            val token = tokenDataStore.getToken() ?: return false  // not logged in
            reviewApi.addReview(
                token = "Bearer $token",  // attach the JWT token in the header
                review = ReviewRequest(
                    movie_id = movieId,
                    text = review.comment,
                    user_name = review.reviewerName,
                    rating = review.rating
                )
            )
            true
        } catch (e: Exception) {
            false  // submission failed (network error or not authorised)
        }
    }

    // Deletes a review by its ID. Also requires the JWT token, and the
    // backend only allows users to delete their own reviews.
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