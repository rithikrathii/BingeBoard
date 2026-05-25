package com.example.cinerate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cinerate.ui.theme.ChipBorder
import com.example.cinerate.ui.theme.GoldAccent
import com.example.cinerate.ui.theme.SecondaryText

@Composable
fun GenreChip(
    genre: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (isSelected) GoldAccent else Color.Transparent)
            .then(
                if (!isSelected) Modifier.border(1.dp, ChipBorder, CircleShape)
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = genre,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Color.Black else SecondaryText
        )
    }
}
