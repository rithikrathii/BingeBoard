package com.example.bingeboard.data.model

data class Movie(
    val id: String = "",
    val title: String = "",
    val plot: String = "",
    val fullplot: String = "",
    val genres: List<String> = emptyList(),
    val runtime: Int = 0,
    val cast: List<String> = emptyList(),
    val poster: String = "",
    val languages: List<String> = emptyList(),
    val directors: List<String> = emptyList(),
    val rated: String = "",
    val year: Int = 0,
    val released: String = "",
    val countries: List<String> = emptyList(),
    val type: String = "",
    val imdb: ImdbRating = ImdbRating(),
    val tomatoes: Tomatoes? = null,
    val awards: Awards? = null,
    val num_mflix_comments: Int = 0
)

data class ImdbRating(
    val rating: Double = 0.0,
    val votes: Int = 0,
    val id: Int = 0
)

data class Awards(
    val wins: Int = 0,
    val nominations: Int = 0,
    val text: String = ""
)

data class Tomatoes(
    val fresh: Int = 0,
    val rotten: Int = 0
)