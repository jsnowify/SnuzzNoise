package com.snowi.snuzz.data.local.entity

data class UserPreferences(
    val id: String? = null,
    val thresholdDecibel: Int = 0,
    val label: String = "",
    val tag: String = "",
    val date: String = "",
    val time: String = ""
)