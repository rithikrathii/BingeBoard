package com.example.cinerate.data.repository

import com.example.cinerate.data.model.Movie
import com.example.cinerate.data.model.Review

interface MovieRepository {
    fun getAllMovies(): List<Movie>
    fun getMovieById(id: Int): Movie?
    fun getReviewsForMovie(movieId: Int): List<Review>
    fun getGenres(): List<String>
}
