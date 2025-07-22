package com.snowi.snuzz.presentation.features.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.snowi.snuzz.data.local.entity.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPreferencesScreen(
    viewModel: SavedPreferencesViewModel = viewModel(),
    onBack: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val preferences = uiState.preferences
    val isLoading = uiState.isLoading
    val error = uiState.error

    var showEditSheet by rememberSaveable { mutableStateOf(false) }
    var editPref by remember { mutableStateOf<UserPreferences?>(null) }

    var thresholdDecibel by rememberSaveable { mutableStateOf("") }
    var tag by rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Saved Preferences") },
                navigationIcon = {
                    onBack?.let {
                        IconButton(onClick = it) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                preferences.isEmpty() -> {
                    Text(
                        text = "No saved preferences.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(preferences, key = { it.id ?: it.hashCode() }) { pref ->
                            PreferenceCard(
                                pref = pref,
                                onEdit = {
                                    editPref = pref
                                    thresholdDecibel = pref.thresholdDecibel.toString()
                                    tag = pref.tag
                                    showEditSheet = true
                                },
                                onDelete = {
                                    coroutineScope.launch {
                                        val result = viewModel.deletePreference(it)
                                        snackbarMessage = if (result.isSuccess) {
                                            "Deleted successfully"
                                        } else {
                                            "Delete failed: ${result.exceptionOrNull()?.message}"
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    snackbarMessage?.let { msg ->
        LaunchedEffect(msg) {
            snackbarHostState.showSnackbar(msg)
            snackbarMessage = null
        }
    }

    if (showEditSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(2.dp)
                        )
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Edit Preference", style = MaterialTheme.typography.headlineSmall)

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showEditSheet = false }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            val threshold = thresholdDecibel.toIntOrNull() ?: 0
                            val updatedPref = editPref?.copy(
                                thresholdDecibel = threshold,
                                tag = tag
                            )
                            if (updatedPref != null) {
                                coroutineScope.launch {
                                    val result = viewModel.updatePreference(updatedPref)
                                    snackbarMessage = if (result.isSuccess) {
                                        showEditSheet = false
                                        "Updated successfully"
                                    } else {
                                        "Update failed: ${result.exceptionOrNull()?.message}"
                                    }
                                }
                            }
                        },
                        enabled = thresholdDecibel.toIntOrNull() != null && tag.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceCard(
    pref: UserPreferences,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(pref.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

            PreferenceRow(Icons.Filled.GraphicEq, "Threshold", "${pref.thresholdDecibel} dB")
            PreferenceRow(Icons.AutoMirrored.Filled.VolumeUp, "Tag", pref.tag)
            PreferenceRow(Icons.AutoMirrored.Filled.EventNote, "Date", pref.date)
            PreferenceRow(Icons.Filled.AccessTime, "Time", pref.time)

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Edit")
                }

                Spacer(Modifier.width(8.dp))

                TextButton(onClick = { pref.id?.let { onDelete(it) } }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun PreferenceRow(icon: ImageVector, label: String, value: String) {
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
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}
