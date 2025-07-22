package com.snowi.snuzz.presentation.components.cards

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MinimalNoiseInfoCard(
    decibel: Float,
    label: String,
    tag: String,
    dateTime: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    // Animatable offsets for smooth bounce-back animation
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(200)) + slideInVertically(
            initialOffsetY = { fullHeight -> with(density) { fullHeight * 0.25f }.toInt() },
            animationSpec = tween(250)
        ),
        exit = fadeOut(tween(150)) + slideOutVertically(
            targetOffsetY = { with(density) { 30.dp.roundToPx() } },
            animationSpec = tween(200)
        )
    ) {
        Card(
            modifier = modifier
                .offset {
                    IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount.x)
                                offsetY.snapTo(offsetY.value + dragAmount.y)
                            }
                        },
                        onDragEnd = {
                            if (offsetY.value > 1000f) {
                                onDismiss()
                            } else {
                                coroutineScope.launch {
                                    launch {
                                        offsetX.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                                    }
                                    launch {
                                        offsetY.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
                .padding(24.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Noise Info", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(16.dp))
                NoiseInfoRow(Icons.Filled.GraphicEq, "Decibel", "%.1f dB".format(decibel))
                NoiseInfoRow(Icons.AutoMirrored.Filled.VolumeUp, "Label", label)
                NoiseInfoRow(Icons.AutoMirrored.Filled.EventNote, "Tag", tag)
                NoiseInfoRow(Icons.Filled.AccessTime, "Time", dateTime)
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun NoiseInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}