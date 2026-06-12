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

    override suspend fun getMovieById(id: String): Movie? {
        return try {
            api.getMovieById(id).toMovie()
        } catch (e: Exception) {
            getAllMovies().find { it.id == id }
        }
    }

    override suspend fun getReviewsForMovie(movieId: String): List<Review> {
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