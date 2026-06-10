package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DreamAnalysis(
    val titleFa: String,
    val titleEn: String,
    val interpretationFa: String,
    val interpretationEn: String,
    val symbols: List<DreamSymbol>,
    val mood: String, // e.g. "Mystical", "Psychological", "Anxious", "Prophetic", "Harmonious"
    val colorHex: String // e.g. "#1E3C72" or similar elegant gradient start color matching the mood
)

@JsonClass(generateAdapter = true)
data class DreamSymbol(
    val symbolFa: String,
    val symbolEn: String,
    val meaningFa: String,
    val meaningEn: String
)
