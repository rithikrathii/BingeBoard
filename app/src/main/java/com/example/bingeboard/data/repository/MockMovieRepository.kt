package com.example.bingeboard.data.repository

import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.Review
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockMovieRepository @Inject constructor() : MovieRepository {

    override suspend fun getAllMovies(): List<Movie> = emptyList()
    override suspend fun getMovieById(id: String): Movie? = null
    override suspend fun getReviewsForMovie(movieId: String): List<Review> = emptyList()
    override suspend fun getGenres(): List<String> = emptyList()
    override suspend fun filterMovies(
        genre: String?,
        yearMin: Int?,
        yearMax: Int?,
        rated: String?,
        language: String?
    ): List<Movie> = emptyList()
    override suspend fun searchMovies(query: String): List<Movie> = emptyList()
    override suspend fun getMoviesByGenre(genre: String): List<Movie> = emptyList()
    override suspend fun addReview(movieId: String, review: Review): Boolean = false
    override suspend fun deleteReview(reviewId: String): Boolean = false
}