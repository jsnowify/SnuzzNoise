// UserPreferencesInputForm.kt
package com.snowi.snuzz.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.snowi.snuzz.data.local.entity.UserPreferences

@Composable
fun UserPreferencesInputForm(
    editingPreference: UserPreferences? = null,
    onSave: (UserPreferences) -> Unit,
    onCancelEdit: () -> Unit
) {
    var threshold by remember { mutableStateOf(TextFieldValue("")) }
    var label by remember { mutableStateOf(TextFieldValue("")) }
    var tag by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(editingPreference) {
        if (editingPreference != null) {
            threshold = TextFieldValue(editingPreference.thresholdDecibel.toString())
            label = TextFieldValue(editingPreference.label)
            tag = TextFieldValue(editingPreference.tag)
        } else {
            threshold = TextFieldValue("")
            label = TextFieldValue("")
            tag = TextFieldValue("")
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = threshold,
            onValueChange = { threshold = it },
            label = { Text("Threshold Decibel") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            label = { Text("Label") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = tag,
            onValueChange = { tag = it },
            label = { Text("Tag") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val thresholdValue = threshold.text.toIntOrNull() ?: 0
                    val currentDate = getCurrentDateTime().first
                    val currentTime = getCurrentDateTime().second
                    val preference = editingPreference?.copy(
                        thresholdDecibel = thresholdValue,
                        label = label.text,
                        tag = tag.text,
                        date = currentDate,
                        time = currentTime
                    ) ?: UserPreferences(
                        thresholdDecibel = thresholdValue,
                        label = label.text,
                        tag = tag.text,
                        date = currentDate,
                        time = currentTime
                    )
                    onSave(preference)
                }
            ) {
                Text(if (editingPreference != null) "Update" else "Add")
            }

            if (editingPreference != null) {
                OutlinedButton(onClick = onCancelEdit) {
                    Text("Cancel")
                }
            }
        }
    }
}

fun getCurrentDateTime(): Pair<String, String> {
    val now = java.time.LocalDateTime.now()
    val date = now.format(java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    val time = now.format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
    return date to time
}
