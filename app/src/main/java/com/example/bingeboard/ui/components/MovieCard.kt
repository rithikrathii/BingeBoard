package com.example.bingeboard.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.ui.theme.CardSurface
import com.example.bingeboard.ui.theme.SecondaryText
import com.example.bingeboard.R

@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val imageRes = when(movie.posterRes){
        0 -> R.drawable.movie
        1 -> R.drawable.movie_2
        2 -> R.drawable.movie_3
        3 -> R.drawable.movie_2
        4 -> R.drawable.movie
        5 -> R.drawable.movie_3
        else -> R.drawable.movie
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Poster Placeholder
        Box(
            modifier = Modifier
                .size(width = 110.dp, height = 110.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color.DarkGray, Color.Black)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(imageRes),
                contentDescription = "movie",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            GenrePill(genre = movie.genre.first())

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = movie.description,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            StarRating(rating = movie.rating)
        }
    }
}
