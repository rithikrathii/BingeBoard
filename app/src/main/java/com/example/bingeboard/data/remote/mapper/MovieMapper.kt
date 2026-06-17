package com.example.bingeboard.data.remote.mapper

import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.ImdbRating
import com.example.bingeboard.data.model.Awards
import com.example.bingeboard.data.model.Tomatoes
import com.example.bingeboard.data.remote.dto.MovieDto

// Converts a MovieDto (the raw JSON shape from the API) into a Movie
// (the clean model our app actually uses). The main job here is to
// fill in safe defaults for any missing or malformed fields, so the
// rest of the app never has to deal with nulls or bad data.
fun MovieDto.toMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title ?: "",          // fall back to empty text if missing
        plot = this.plot ?: "",
        fullplot = this.fullplot ?: "",
        genres = this.genres ?: emptyList(),
        runtime = this.runtime ?: 0,
        cast = this.cast ?: emptyList(),
        poster = this.poster ?: "",
        languages = this.languages ?: emptyList(),
        directors = this.directors ?: emptyList(),
        rated = this.rated ?: "NR",        // "NR" = Not Rated

        // Some movies in the dataset have broken year values like
        // "1981è" or "1994è1998". We strip out everything except the
        // digits and parse that. If there's nothing usable, we default
        // to 0 instead of letting the app crash.
        year = try {
            val digits = this.year?.filter { it.isDigit() } ?: ""
            if (digits.isEmpty()) 0 else digits.toInt()
        } catch (e: Exception) { 0 },

        released = this.released ?: "",
        countries = this.countries ?: emptyList(),
        type = this.type ?: "",

        // IMDb fields can also be missing or malformed, so each one is
        // parsed defensively and falls back to 0 / 0.0 if it can't be read.
        imdb = ImdbRating(
            rating = try { this.imdb?.rating?.toDoubleOrNull() ?: 0.0 } catch (e: Exception) { 0.0 },
            votes = try { this.imdb?.votes?.filter { it.isDigit() }?.toIntOrNull() ?: 0 } catch (e: Exception) { 0 },
            id = try { this.imdb?.id?.filter { it.isDigit() }?.toIntOrNull() ?: 0 } catch (e: Exception) { 0 }
        ),

        num_mflix_comments = this.num_mflix_comments ?: 0
    )
}