package com.example.cinerate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cinerate.ui.theme.*

@Composable
fun GenrePill(
    genre: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (genre) {
        "Sci-Fi" -> GenreSciFi.copy(alpha = 0.2f)
        "Action" -> GenreAction.copy(alpha = 0.2f)
        "Drama" -> GenreDrama.copy(alpha = 0.2f)
        "Thriller" -> GenreThriller.copy(alpha = 0.2f)
        "Comedy" -> GenreComedy.copy(alpha = 0.2f)
        else -> GoldAccent.copy(alpha = 0.2f)
    }
    
    val textColor = when (genre) {
        "Sci-Fi" -> GenreSciFi
        "Action" -> GenreAction
        "Drama" -> GenreDrama
        "Thriller" -> GenreThriller
        "Comedy" -> GenreComedy
        else -> GoldAccent
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = genre,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
