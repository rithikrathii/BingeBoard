package com.example.bingeboard.data.remote.api

import com.example.bingeboard.data.remote.dto.MovieDto
import com.example.bingeboard.data.remote.dto.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movies/")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): MovieListResponse

    @GET("movies/search")
    suspend fun searchMovies(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): MovieListResponse

    @GET("movies/filter")
    suspend fun filterMovies(
        @Query("genre") genre: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): MovieListResponse

    @GET("movies/genres")
    suspend fun getGenres(): List<String>

    @GET("movies/{id}")
    suspend fun getMovieById(@Path("id") id: String): MovieDto
}
