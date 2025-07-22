package com.snowi.snuzz.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.snowi.snuzz.data.local.entity.UserPreferences
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val preferencesCollection = firestore.collection("userPreferences")

    suspend fun upsertPreferences(
        id: String?,
        preferences: UserPreferences
    ): Result<Unit> = runCatching {
        val data = mapOf(
            "thresholdDecibel" to preferences.thresholdDecibel,
            "tag" to preferences.tag,
            "date" to preferences.date,
            "time" to preferences.time
        )

        if (id == null) {
            preferencesCollection.add(data).await()
        } else {
            preferencesCollection.document(id).set(data).await()
        }
    }

    suspend fun updatePreference(preferences: UserPreferences): Result<Unit> {
        return try {
            if (preferences.id == null) return Result.failure(Exception("No ID to update"))
            val data = hashMapOf(
                "thresholdDecibel" to preferences.thresholdDecibel,
                "tag" to preferences.tag,
                "date" to preferences.date,
                "time" to preferences.time
            )
            preferencesCollection.document(preferences.id).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPreferences(): Result<List<UserPreferences>> = runCatching {
        val snapshot = preferencesCollection.get().await()
        snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            val threshold = data["thresholdDecibel"]?.toString()?.toIntOrNull() ?: return@mapNotNull null
            UserPreferences(
                id = doc.id,
                thresholdDecibel = threshold,
                label = getNoiseLabel(threshold),
                tag = data["tag"] as? String ?: "",
                date = data["date"] as? String ?: "",
                time = data["time"] as? String ?: ""
            )
        }
    }

    suspend fun deletePreference(id: String): Result<Unit> = runCatching {
        preferencesCollection.document(id).delete().await()
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