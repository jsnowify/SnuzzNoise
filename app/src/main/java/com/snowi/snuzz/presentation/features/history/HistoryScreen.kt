package com.snowi.snuzz.presentation.features.history

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.snowi.snuzz.presentation.components.ActivityCard
import com.snowi.snuzz.presentation.components.Title

@Composable
fun HistoryScreen (modifier: Modifier = Modifier){
    Column {

    }
//    Title("Good day, Snowi")
    ActivityCard(
        dbLevel = 40,
        noiseLabel = "NORMAL",
        activityLabel = "MEETING",
        date = "July 02, 2025",
        time = "10:04 AM"
    )

}