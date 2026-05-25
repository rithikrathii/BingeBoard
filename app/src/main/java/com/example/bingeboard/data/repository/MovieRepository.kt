package com.example.bingeboard.data.repository

import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.Review

interface MovieRepository {
    fun getAllMovies(): List<Movie>
    fun getMovieById(id: Int): Movie?
    fun getReviewsForMovie(movieId: Int): List<Review>
    fun getGenres(): List<String>
}
