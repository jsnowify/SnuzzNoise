package com.snowi.snuzz.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ActivityCard(
    dbLevel: Int,
    noiseLabel: String,
    activityLabel: String,
    date: String,
    time: String
) {
    val containerColor = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface
    val labelBackgroundColor = MaterialTheme.colorScheme.onBackground

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, labelBackgroundColor.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Noise Label
            Box(
                modifier = Modifier
                    .background(labelBackgroundColor, shape = RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = noiseLabel,
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            // Decibel Row
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dbLevel.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    color = contentColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "dB",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
            }
            // Date & Time
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = date, style = MaterialTheme.typography.bodySmall, color = contentColor)
                Text(text = time, style = MaterialTheme.typography.bodySmall, color = contentColor)
            }
            // Activity Label
            Box(
                modifier = Modifier
                    .background(labelBackgroundColor, shape = RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = activityLabel,
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
