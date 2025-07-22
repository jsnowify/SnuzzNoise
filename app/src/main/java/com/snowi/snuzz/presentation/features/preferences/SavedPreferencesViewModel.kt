// File: SavedPreferencesViewModel.kt
package com.snowi.snuzz.presentation.features.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snowi.snuzz.data.local.entity.UserPreferences
import com.snowi.snuzz.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedPreferencesUiState(
    val isLoading: Boolean = true,
    val preferences: List<UserPreferences> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SavedPreferencesViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedPreferencesUiState())
    val uiState: StateFlow<SavedPreferencesUiState> = _uiState

    val userPreferences: StateFlow<List<UserPreferences>> = _uiState.map { it.preferences }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        loadPreferences()
    }

    fun loadPreferences() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getPreferences()
            result.fold(
                onSuccess = { _uiState.value = SavedPreferencesUiState(isLoading = false, preferences = it) },
                onFailure = { _uiState.value = SavedPreferencesUiState(isLoading = false, error = it.message) }
            )
        }
    }

    fun updatePreference(pref: UserPreferences): Result<Unit> {
        return runCatching {
            viewModelScope.launch {
                repository.updatePreference(pref)
                loadPreferences()
            }
        }
    }

    fun deletePreference(id: String): Result<Unit> {
        return runCatching {
            viewModelScope.launch {
                repository.deletePreference(id)
                loadPreferences()
            }
        }
    }
}
