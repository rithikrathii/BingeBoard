package com.example.bingeboard.data.repository

import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.Review

interface MovieRepository {
    suspend fun getAllMovies(): List<Movie>
    suspend fun getMovieById(id: String): Movie?
    suspend fun getReviewsForMovie(movieId: String): List<Review>
    suspend fun getGenres(): List<String>
    suspend fun searchMovies(query: String): List<Movie>
    suspend fun getMoviesByGenre(genre: String): List<Movie>
    suspend fun filterMovies(
        genre: String? = null,
        yearMin: Int? = null,
        yearMax: Int? = null,
        rated: String? = null,
        language: String? = null
    ): List<Movie>
    suspend fun addReview(movieId: String, review: Review): Boolean

    suspend fun deleteReview(reviewId: String): Boolean
}