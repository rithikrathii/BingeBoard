package com.example.bingeboard.data.remote.mapper

import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.ImdbRating
import com.example.bingeboard.data.model.Awards
import com.example.bingeboard.data.model.Tomatoes
import com.example.bingeboard.data.remote.dto.MovieDto

fun MovieDto.toMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title ?: "",
        plot = this.plot ?: "",
        fullplot = this.fullplot ?: "",
        genres = this.genres ?: emptyList(),
        runtime = this.runtime ?: 0,
        cast = this.cast ?: emptyList(),
        poster = this.poster ?: "",
        languages = this.languages ?: emptyList(),
        directors = this.directors ?: emptyList(),
        rated = this.rated ?: "NR",
        year = this.year ?: 0,
        released = this.released ?: "",
        countries = this.countries ?: emptyList(),
        type = this.type ?: "",
        imdb = ImdbRating(
            rating = this.imdb?.rating ?: 0.0,
            votes = this.imdb?.votes ?: 0,
            id = this.imdb?.id ?: 0
        ),
        num_mflix_comments = this.num_mflix_comments ?: 0
    )
}