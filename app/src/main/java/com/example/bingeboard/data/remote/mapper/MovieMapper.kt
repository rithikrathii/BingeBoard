package com.example.bingeboard.data.remote.mapper

import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.remote.dto.MovieDto

fun MovieDto.toMovie(): Movie {
    return Movie(
        id = this.id.hashCode(),      // convert String _id to Int for nav
        mongoId = this.id,            // keep original String ID
        title = this.title,
        genre = this.genres,
        year = this.year,
        duration = this.runtime?.let { "${it / 60}h ${it % 60}m" } ?: "N/A",
        rating = 0.0,                 // movie catalogue has no rating field
        reviewCount = "N/A",
        ageRating = this.rated ?: "NR",
        description = this.plot ?: "No description available.",
        posterUrl = this.poster ?: "",
        isTopRated = false,
        topRatedLabel = ""
    )
}
