package com.example.bingeboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.ui.theme.CardSurface
import com.example.bingeboard.ui.theme.SecondaryText

@Composable
fun StatsBox(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(
            value = { StarRating(rating = movie.imdb.rating, showMaxRating = true) },
            label = "IMDb Rating"
        )

        Box(modifier = Modifier.width(1.dp).height(24.dp).background(SecondaryText.copy(alpha = 0.3f)))

        StatItem(
            value = {
                Text(
                    text = "${movie.imdb.votes}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            },
            label = "Reviews"
        )

        Box(modifier = Modifier.width(1.dp).height(24.dp).background(SecondaryText.copy(alpha = 0.3f)))

        StatItem(
            value = {
                Text(
                    text = movie.rated,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            },
            label = "Rated"
        )
    }
}

@Composable
private fun StatItem(
    value: @Composable () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        value()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = SecondaryText
        )
    }
}