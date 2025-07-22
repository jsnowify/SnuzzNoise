package com.snowi.snuzz.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DecibelBarChart(
    history: List<Float>,
    modifier: Modifier = Modifier,
    maxBars: Int = 7,
    barWidth: Dp = 12.dp,
    maxDb: Float = 100f,
    labels: List<String> = List(maxBars) { "" },
    barColor: Color = MaterialTheme.colorScheme.primary // customizable color
) {
    val displayedHistory = remember(history) {
        val recent = if (history.size > maxBars) history.takeLast(maxBars) else history
        recent + List(maxBars - recent.size) { 0f }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barSpace = canvasWidth / maxBars
            val barWidthPx = barWidth.toPx()

            displayedHistory.forEachIndexed { index, value ->
                val normalizedHeight = (value / maxDb).coerceIn(0f, 1f)
                val barHeight = canvasHeight * normalizedHeight

                val x = index * barSpace + (barSpace - barWidthPx) / 2f
                val y = canvasHeight - barHeight

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidthPx, barHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Labels below each bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            labels.take(maxBars).forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 12.sp
                )
            }
        }
    }
}
