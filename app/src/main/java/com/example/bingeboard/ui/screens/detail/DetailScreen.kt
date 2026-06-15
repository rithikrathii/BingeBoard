package com.example.bingeboard.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bingeboard.ui.components.*
import com.example.bingeboard.ui.theme.Background
import com.example.bingeboard.ui.theme.CardSurface
import com.example.bingeboard.ui.theme.GoldAccent
import com.example.bingeboard.ui.theme.SecondaryText

@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val movie = uiState.movie ?: return

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
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
                        AsyncImage(
                            model = movie.poster,
                            contentDescription = movie.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

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
                        movie.genres.forEach { genre ->
                            GenrePill(genre = genre)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${movie.year}  ·  ${movie.runtime} min  ·  ${movie.rated}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    StatsBox(movie = movie)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = movie.plot,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    SectionHeader(
                        title = "Reviews",
                        onActionClick = { }
                    )
                }
            }

            items(uiState.reviews, key = { it.id }) { review ->
                ReviewCard(
                    review = review,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    currentUserEmail = uiState.currentUserEmail,
                    onDeleteClick = { reviewId -> viewModel.deleteReview(reviewId) }
                )
            }

            // Write Review Form
            if (uiState.currentUserEmail.isNotEmpty()) item {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Write a Review",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Star Rating Selector
                        Row {
                            (1..5).forEach { star ->
                                IconButton(onClick = { viewModel.onRatingChanged(star) }) {
                                    Icon(
                                        imageVector = if (star <= uiState.reviewRating)
                                            Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Review Text Input
                        TextField(
                            value = uiState.reviewText,
                            onValueChange = { viewModel.onReviewTextChanged(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = {
                                Text(
                                    "Write your review here...",
                                    color = SecondaryText
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Background,
                                unfocusedContainerColor = Background,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = GoldAccent
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.submitReview() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = Color.Black
                            ),
                            enabled = !uiState.isSubmitting
                        ) {
                            if (uiState.isSubmitting) {
                                CircularProgressIndicator(
                                    color = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    "Submit Review",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (uiState.submitSuccess) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Review submitted successfully!",
                                color = GoldAccent,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 22.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onBackClick() }
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