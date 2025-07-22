package com.snowi.snuzz.presentation.features.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snowi.snuzz.presentation.components.MorphingVisualizerCard
import com.snowi.snuzz.presentation.components.Title
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: AudioVisualizerViewModel = hiltViewModel()
) {
    val amplitude by viewModel.amplitude.collectAsState()
    val decibel by viewModel.decibel.collectAsState()
    val isAlarmPlaying by viewModel.isAlarmPlaying.collectAsState()

    val label by remember(decibel) { derivedStateOf { getNoiseLabel(decibel) } }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.startRecording(context)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize()
        ) {
            Title(
                headerTitle = "Good day, Snowi",
                subText = "Monitoring Office Noise",
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            MorphingVisualizerCard(
                amplitude = amplitude,
                decibel = decibel,
                label = label,
                tag = "Office",
                dateTime = getCurrentDateTime(),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            if (isAlarmPlaying) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.stopAlarm() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 24.dp)
                ) {
                    Text("Stop Alarm")
                }
            }
        }
    }
}

private val dateFormat = SimpleDateFormat("MMMM dd, yyyy h:mm a", Locale.getDefault())
fun getCurrentDateTime(): String = dateFormat.format(Date())

fun getNoiseLabel(decibel: Float): String = when {
    decibel < 30 -> "Quiet"
    decibel < 60 -> "Normal"
    decibel < 85 -> "Loud"
    else -> "Very Loud"
}
