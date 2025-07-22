package com.snowi.snuzz.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.snowi.snuzz.data.local.entity.UserPreferences

@Composable
fun PreferenceForm(
    editing: UserPreferences?,
    onSubmit: (UserPreferences) -> Unit
) {
    var decibel by remember { mutableStateOf(editing?.thresholdDecibel?.toString() ?: "") }
    var tag by remember { mutableStateOf(editing?.tag ?: "") }
    var date by remember { mutableStateOf(editing?.date ?: "") }
    var time by remember { mutableStateOf(editing?.time ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = decibel,
            onValueChange = { decibel = it },
            label = { Text("Decibel Threshold") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = tag,
            onValueChange = { tag = it },
            label = { Text("Tag") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                val preference = UserPreferences(
                    id = editing?.id,
                    thresholdDecibel = decibel.toIntOrNull() ?: 0,
                    tag = tag,
                    date = date,
                    time = time
                )
                onSubmit(preference)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (editing != null) "Update" else "Save")
        }
    }
}
