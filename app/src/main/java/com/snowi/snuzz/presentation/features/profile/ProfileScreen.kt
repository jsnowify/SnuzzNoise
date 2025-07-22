package com.snowi.snuzz.presentation.features.profile

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snowi.snuzz.data.local.entity.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel(), onManageSavedPreferences: () -> Unit = {}) {
    val uiState by viewModel.uiState.collectAsState()
    val editingId = uiState.editingId
    val snackbarHostState = remember { SnackbarHostState() }

    var thresholdDecibel by rememberSaveable { mutableStateOf("") }
    var tag by rememberSaveable { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var toDeleteId by remember { mutableStateOf<String?>(null) }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "User Preferences",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = thresholdDecibel,
                    onValueChange = { thresholdDecibel = it },
                    label = { Text("Threshold Decibel (dB)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Tag") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    viewModel.savePreferences(thresholdDecibel.toInt(), tag)
                    thresholdDecibel = ""
                    tag = ""
                    snackbarMessage = if (editingId != null) "Updated successfully" else "Saved successfully"
                },
                modifier = Modifier.align(Alignment.End),
                enabled = thresholdDecibel.toIntOrNull() != null && tag.isNotBlank()
            ) {
                Text(if (editingId != null) "Update" else "Save")
            }

            Button(
                onClick = onManageSavedPreferences,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Saved Preferences")
            }

            // 🧹 No more CircularProgressIndicator here
        }
    }

    snackbarMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            snackbarMessage = null
        }
    }

    if (showDeleteDialog && toDeleteId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePreference(toDeleteId!!)
                    snackbarMessage = "Deleted successfully"
                    showDeleteDialog = false
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            },
            title = { Text("Confirm Deletion") },
            text = {
                Text("Are you sure you want to delete this preference? This action cannot be undone.")
            }
        )
    }
}

@Composable
fun PreferenceCard(
    pref: UserPreferences,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                pref.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            PreferenceRow(Icons.Filled.Edit, "Threshold", "${pref.thresholdDecibel} dB")
            PreferenceRow(Icons.Filled.Edit, "Tag", pref.tag)
            PreferenceRow(Icons.Filled.Edit, "Date", pref.date)
            PreferenceRow(Icons.Filled.Edit, "Time", pref.time)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit")
                }

                Spacer(modifier = Modifier.width(8.dp))

                pref.id?.let { id ->
                    TextButton(onClick = { onDelete(id) }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 6.dp)
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
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
