package com.snowi.snuzz.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.snowi.snuzz.presentation.components.cards.MinimalNoiseInfoCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MorphingVisualizerCard(
    amplitude: Int,
    decibel: Float,
    label: String,
    tag: String,
    dateTime: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showCard by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isExpanded) 0f else offsetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "offsetX"
    )
    val animatedOffsetY by animateFloatAsState(
        targetValue = if (isExpanded) 0f else offsetY,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "offsetY"
    )

    val circleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = animatedOffsetX
                translationY = animatedOffsetY
            }
            .pointerInput(isExpanded) {
                if (!isExpanded) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        },
                        onDragEnd = {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    )
                }
            }
            .then(
                if (isExpanded) Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp)
                else Modifier.size(280.dp) // Increased from 180.dp to 280.dp
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                if (!isExpanded) {
                    isExpanded = true
                    showCard = true
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = showCard,
            transitionSpec = {
                (fadeIn(tween(150)) + scaleIn(tween(200), initialScale = 0.95f)) togetherWith
                        (fadeOut(tween(150)) + scaleOut(tween(200), targetScale = 0.95f))
            },
            contentAlignment = Alignment.Center,
            label = "MorphingCardTransition"
        ) { expanded ->
            if (expanded) {
                MinimalNoiseInfoCard(
                    decibel = decibel,
                    label = label,
                    tag = tag,
                    dateTime = dateTime,
                    onDismiss = {
                        showCard = false
                        coroutineScope.launch {
                            delay(250)
                            isExpanded = false
                        }
                    }
                )
            } else {
                val baseRadius = 120f // <- MUCH larger circle base
                val clampedAmplitude = amplitude.coerceIn(0, 120)
                val targetRadius = baseRadius + clampedAmplitude * 0.4f

                val animatedRadius by animateFloatAsState(
                    targetValue = targetRadius,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    label = "circleRadius"
                )

                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulse by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "pulseProgress"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val pulseRadius = animatedRadius + pulse * 60f

                    drawCircle(
                        color = circleColor.copy(alpha = (1f - pulse) * 0.25f),
                        radius = pulseRadius,
                        center = center
                    )

                    drawCircle(
                        color = circleColor,
                        radius = animatedRadius,
                        center = center
                    )
                }
            }
        }
    }
}
