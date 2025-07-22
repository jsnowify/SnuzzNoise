// File: ProfileViewModel.kt
package com.snowi.snuzz.presentation.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowi.snuzz.data.local.entity.UserPreferences
import com.snowi.snuzz.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        loadPreferences()
    }

    fun loadPreferences() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = repository.getPreferences()
            result.fold(
                onSuccess = { list ->
                    _uiState.update {
                        it.copy(isLoading = false, preferences = list, error = null)
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, error = exception.message ?: "Unknown error")
                    }
                }
            )
        }
    }

    fun savePreferences(threshold: Int, tag: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val now = Date()
            val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val editingId = _uiState.value.editingId

            val newPref = UserPreferences(
                id = editingId,
                thresholdDecibel = threshold,
                label = getNoiseLabel(threshold),
                tag = tag,
                date = dateFormat.format(now),
                time = timeFormat.format(now)
            )

            val result = repository.upsertPreferences(editingId, newPref)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(editingId = null) }
                    loadPreferences()
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, error = exception.message ?: "Failed to save preferences")
                    }
                }
            )
        }
    }

    fun deletePreference(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.deletePreference(id)
            result.fold(
                onSuccess = { loadPreferences() },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, error = exception.message ?: "Failed to delete preference")
                    }
                }
            )
        }
    }

    fun startEditing(pref: UserPreferences, fillInputs: (String, String) -> Unit) {
        _uiState.value = _uiState.value.copy(editingId = pref.id)
        fillInputs(pref.thresholdDecibel.toString(), pref.tag)
    }

    private fun getNoiseLabel(decibel: Int): String = when {
        decibel < 30 -> "Very Quiet"
        decibel in 30..59 -> "Quiet"
        decibel in 60..79 -> "Moderate"
        decibel in 80..89 -> "Loud"
        decibel >= 90 -> "Very Loud"
        else -> "Unknown"
    }
}
