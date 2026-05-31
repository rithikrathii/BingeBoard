package com.example.bingeboard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    @SerializedName("_id")     val id: String,
    @SerializedName("title")   val title: String,
    @SerializedName("year")    val year: Int,
    @SerializedName("genres")  val genres: List<String>,
    @SerializedName("cast")    val cast: List<String>,
    @SerializedName("poster")  val poster: String?,
    @SerializedName("directors") val directors: List<String>,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("languages") val languages: List<String>,
    @SerializedName("rated")   val rated: String?,
    @SerializedName("plot")    val plot: String?
)

data class MovieListResponse(
    @SerializedName("movies") val movies: List<MovieDto>,
    @SerializedName("total")  val total: Int,
    @SerializedName("page")   val page: Int,
    @SerializedName("limit")  val limit: Int
)

data class GenresResponse(
    val genres: List<String>
)
