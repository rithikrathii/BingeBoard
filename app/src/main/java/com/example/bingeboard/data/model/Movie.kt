package com.example.bingeboard.data.model

data class Movie(
    val id: Int,
    val title: String,
    val genre: List<String>,        // e.g. ["Sci-Fi", "Thriller"]
    val year: Int,
    val duration: String,           // e.g. "2h 49m"
    val rating: Double,             // IMDb-style, e.g. 8.7
    val reviewCount: String,        // e.g. "1.6M"
    val ageRating: String,          // e.g. "PG-13"
    val description: String,
    val posterRes: Int,             // R.drawable.xxx — placeholder drawable for now
    val isTopRated: Boolean = false,
    val topRatedLabel: String = "Top 20"
)
