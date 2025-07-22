package com.snowi.snuzz.presentation.features.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.snowi.snuzz.presentation.components.ActivityCard

@Composable
fun NotificationScreen (modifier: Modifier = Modifier){
    ActivityCard(
        dbLevel = 40,
        noiseLabel = "NORMAL",
        activityLabel = "MEETING",
        date = "July 02, 2025",
        time = "10:04 AM"
    )

}