package com.example.bingeboard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    @SerializedName("_id")     val id: String = "",
    @SerializedName("title")   val title: String? = null,
    @SerializedName("year") val year: String? = null,
    @SerializedName("genres")  val genres: List<String>? = null,
    @SerializedName("cast")    val cast: List<String>? = null,
    @SerializedName("poster")  val poster: String? = null,
    @SerializedName("directors") val directors: List<String>? = null,
    @SerializedName("runtime") val runtime: Int? = null,
    @SerializedName("languages") val languages: List<String>? = null,
    @SerializedName("rated")   val rated: String? = null,
    @SerializedName("plot")    val plot: String? = null,
    @SerializedName("fullplot") val fullplot: String? = null,
    @SerializedName("released") val released: String? = null,
    @SerializedName("countries") val countries: List<String>? = null,
    @SerializedName("type")    val type: String? = null,
    @SerializedName("imdb")    val imdb: ImdbDto? = null,
    @SerializedName("awards")  val awards: AwardsDto? = null,
    @SerializedName("num_mflix_comments") val num_mflix_comments: Int? = null
)

data class ImdbDto(
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("votes")  val votes: String? = null,
    @SerializedName("id")     val id: String? = null
)

data class AwardsDto(
    @SerializedName("wins")         val wins: Int? = null,
    @SerializedName("nominations")  val nominations: Int? = null,
    @SerializedName("text")         val text: String? = null
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