package com.example.bingeboard.ui.screens.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bingeboard.R
import com.example.bingeboard.ui.components.*
import com.example.bingeboard.ui.theme.Background
import com.example.bingeboard.ui.theme.SecondaryText

@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val movie = uiState.movie ?: return


    val imageRes = when(movie.posterRes){
        0 -> R.drawable.movie
        1 -> R.drawable.movie_2
        2 -> R.drawable.movie_3
        3 -> R.drawable.movie_2
        4 -> R.drawable.movie
        5 -> R.drawable.movie_3
        else -> R.drawable.movie
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Hero Image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(440.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Gray.copy(alpha = 0.5f), Background)
                            )
                        )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Background),
                                startY = 300f
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
                }
            }

            // Movie Info
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .offset(y = (-40).dp)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        movie.genre.forEach { genre ->
                            GenrePill(genre = genre)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${movie.year}  ·  ${movie.duration}  ·  ${movie.ageRating}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    StatsBox(movie = movie)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = movie.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    SectionHeader(
                        title = "Reviews",
                        onActionClick = { /* TODO */ }
                    )

                }
            }

            // Reviews List
            items(uiState.reviews, key = { it.id }) { review ->
                ReviewCard(
                    review = review,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 22.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable{onBackClick()}
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
