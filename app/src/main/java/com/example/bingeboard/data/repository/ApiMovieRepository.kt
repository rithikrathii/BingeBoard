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

@Singleton
class ApiMovieRepository @Inject constructor(
    private val api: MovieApiService,
    private val reviewApi: ReviewApiService,
    private val tokenDataStore: TokenDataStore
) : MovieRepository {

    override suspend fun getAllMovies(): List<Movie> {
        return try {
            val allMovies = mutableListOf<Movie>()
            for (page in 1..10) {
                val response = api.getMovies(page = page, limit = 50)
                if (response.isEmpty()) break
                allMovies.addAll(response.map { it.toMovie() })
            }
            allMovies
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieById(id: String): Movie? {
        return try {
            api.getMovieById(id).toMovie()
        } catch (e: Exception) {
            getAllMovies().find { it.id == id }
        }
    }

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

    override suspend fun getGenres(): List<String> {
        return try {
            api.getGenres()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        return try {
            api.searchMovies(query = query, page = 1, limit = 50).map { it.toMovie() }
        } catch (e: Exception) {
            emptyList()
        }
    }

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

    override suspend fun filterMovies(
        genre: String?,
        yearMin: Int?,
        yearMax: Int?,
        rated: String?,
        language: String?
    ): List<Movie> {
        return try {
            val allMovies = mutableListOf<Movie>()
            for (page in 1..10) {
                val response = api.filterMovies(
                    genre = genre,
                    yearMin = yearMin,
                    yearMax = yearMax,
                    rated = rated,
                    language = language,
                    page = page,
                    limit = 50
                )
                if (response.isEmpty()) break
                allMovies.addAll(response.map { it.toMovie() })
            }
            allMovies
        } catch (e: Exception) {
            emptyList()
        }
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
