package com.example.bingeboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bingeboard.data.model.Review
import com.example.bingeboard.ui.theme.CardSurface
import com.example.bingeboard.ui.theme.GoldAccent
import com.example.bingeboard.ui.theme.SecondaryText

@Composable
fun ReviewCard(
    review: Review,
    modifier: Modifier = Modifier,
    currentUserEmail: String = "",
    onDeleteClick: ((String) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GoldAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = review.reviewerInitials,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.reviewerName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = review.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = if (index < review.rating) GoldAccent else SecondaryText,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Show delete button only for current user's reviews
                if (currentUserEmail.isNotEmpty() &&
                    review.userId == currentUserEmail &&
                    onDeleteClick != null) {
                    IconButton(
                        onClick = { onDeleteClick(review.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete review",
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = review.comment,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}