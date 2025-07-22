package com.snowi.snuzz.presentation.features.profile

import com.snowi.snuzz.data.local.entity.UserPreferences

data class UiState(
   val isLoading: Boolean = true,
   val preferences: List<UserPreferences> = emptyList(),
   val error: String? = null,
   val editingId: String? = null
)
