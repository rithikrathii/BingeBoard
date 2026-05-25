package com.example.bingeboard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bingeboard.ui.theme.SecondaryText

@Composable
fun TeamMemberRow(
    name: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
        if (showDivider) {
            HorizontalDivider(color = SecondaryText.copy(alpha = 0.2f))
        }
    }
}
