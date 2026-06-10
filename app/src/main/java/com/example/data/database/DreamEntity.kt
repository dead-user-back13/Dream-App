package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dreams")
data class DreamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userPrompt: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false,
    val titleFa: String,
    val titleEn: String,
    val interpretationFa: String,
    val interpretationEn: String,
    val mood: String,
    val colorHex: String,
    val symbolsJson: String // Serialized List<DreamSymbol>
)
