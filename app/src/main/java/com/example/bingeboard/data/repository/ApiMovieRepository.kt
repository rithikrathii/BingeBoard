package com.example.bingeboard.data.repository

import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.Review
import com.example.bingeboard.data.remote.api.MovieApiService
import com.example.bingeboard.data.remote.mapper.toMovie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiMovieRepository @Inject constructor(
    private val api: MovieApiService
) : MovieRepository {

    override suspend fun getAllMovies(): List<Movie> {
        return try {
            val response = api.getMovies(limit = 50)
            response.movies.map { it.toMovie() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieById(id: Int): Movie? {
        // Since the interface uses Int id, and API uses String _id, 
        // we might have an issue here if we don't have the mongoId.
        // For now, let's assume we search in all movies.
        return getAllMovies().find { it.id == id }
    }

    override suspend fun getReviewsForMovie(movieId: Int): List<Review> {
        // Movie catalogue API has no reviews. Return empty list.
        return emptyList()
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
            val response = api.searchMovies(query = query)
            response.movies.map { it.toMovie() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMoviesByGenre(genre: String): List<Movie> {
        return try {
            val response = api.filterMovies(genre = genre)
            response.movies.map { it.toMovie() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
