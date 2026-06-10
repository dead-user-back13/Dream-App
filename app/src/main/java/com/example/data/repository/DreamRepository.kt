package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.GeminiRequest
import com.example.data.api.MoshiContent
import com.example.data.api.MoshiGenerationConfig
import com.example.data.api.MoshiPart
import com.example.data.api.MoshiSystemInstruction
import com.example.data.api.RetrofitClient
import com.example.data.database.DreamDao
import com.example.data.database.DreamEntity
import com.example.data.model.DreamAnalysis
import com.example.data.model.DreamSymbol
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DreamRepository(private val dreamDao: DreamDao) {

    val allDreams: Flow<List<DreamEntity>> = dreamDao.getAllDreams()
    val bookmarkedDreams: Flow<List<DreamEntity>> = dreamDao.getBookmarkedDreams()

    private val symbolsListAdapter = RetrofitClient.moshi.adapter<List<DreamSymbol>>(
        Types.newParameterizedType(List::class.java, DreamSymbol::class.java)
    )

    private val dreamAnalysisAdapter = RetrofitClient.moshi.adapter(DreamAnalysis::class.java)

    fun getDreamById(id: Long): Flow<DreamEntity?> {
        return dreamDao.getDreamById(id)
    }

    suspend fun saveDreamAnalysis(userPrompt: String, analysis: DreamAnalysis, isBookmarked: Boolean = false): Long {
        val symbolsJson = symbolsListAdapter.toJson(analysis.symbols) ?: "[]"
        val entity = DreamEntity(
            userPrompt = userPrompt,
            titleFa = analysis.titleFa,
            titleEn = analysis.titleEn,
            interpretationFa = analysis.interpretationFa,
            interpretationEn = analysis.interpretationEn,
            mood = analysis.mood,
            colorHex = analysis.colorHex,
            symbolsJson = symbolsJson,
            isBookmarked = isBookmarked
        )
        return dreamDao.insertDream(entity)
    }

    suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean) {
        dreamDao.updateBookmarkStatus(id, isBookmarked)
    }

    suspend fun deleteDream(dream: DreamEntity) {
        dreamDao.deleteDream(dream)
    }

    suspend fun deleteDreamById(id: Long) {
        dreamDao.deleteDreamById(id)
    }

    suspend fun clearHistory() {
        dreamDao.clearHistory()
    }

    fun parseSymbols(json: String): List<DreamSymbol> {
        return try {
            symbolsListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun interpretDream(dreamDescription: String, customApiKey: String? = null): Result<DreamAnalysis> = withContext(Dispatchers.IO) {
        val activeApiKey = when {
            !customApiKey.isNullOrBlank() -> customApiKey
            BuildConfig.GEMINI_API_KEY.isNotEmpty() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        if (activeApiKey.isBlank()) {
            return@withContext Result.failure(Exception("API_KEY_MISSING"))
        }

        val systemPrompt = """
            You are a wise, psychological, and compassionate master of Dream Interpretation (تعبیر خواب).
            The user will describe their dream in Persian (Farsi), English, or a mix of both.
            
            You must analyze the dream and output a JSON object adhering exactly to the following schema structure.
            Do not include any greeting or conversational filler in your response; return ONLY the raw JSON string.
            
            JSON keys:
            - "titleFa": A short poetic, beautiful title in Persian (e.g. "پرواز در آسمان لاجوردی").
            - "titleEn": A short poetic, beautiful title in English (e.g. "Soaring through Azure Skies").
            - "interpretationFa": A deep, narrative, compassionate and highly detailed interpretation in Persian. Use exquisite, elegant Persian prose. Include psychological insights, spiritual associations, and constructive/hopeful advice.
            - "interpretationEn": The equivalent deep, narrative interpretation in English, written in beautiful, clinical, yet poetic prose.
            - "symbols": An array of important dream objects, symbols, or elements. Each symbol contains:
                - "symbolFa": Name of the symbol in Persian.
                - "symbolEn": Name of the symbol in English.
                - "meaningFa": Meaning/symbolism of this element in Persian.
                - "meaningEn": Meaning/symbolism of this element in English.
            - "mood": One of: "Mystical", "Aesthetic", "Anxious", "Prophetic", "Harmonious", "Melancholy".
            - "colorHex": An elegant starting hex color for dynamic theme gradient reflecting the mood of the dream (choose a gorgeous dark shade: e.g. "#1A2E40" for Mystical, "#2D1D38" for Aesthetic, "#2B161A" for Anxious, "#0F2826" for Prophetic, "#1B2A1C" for Harmonious, "#28231C" for Melancholy). Only output hex codes in uppercase, e.g., "#123456".
            
            Strictly respond in correct JSON. Do not write any markdown codeblock wraps (like ```json ... ```) — just pure JSON text. Ensure everything is correctly escaped and valid JSON.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                MoshiContent(
                    parts = listOf(
                        MoshiPart(text = "Interpret this dream: $dreamDescription")
                    )
                )
            ),
            generationConfig = MoshiGenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.8f
            ),
            systemInstruction = MoshiSystemInstruction(
                parts = listOf(
                    MoshiPart(text = systemPrompt)
                )
            )
        )

        try {
            val response = RetrofitClient.service.generateContent(activeApiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return@withContext Result.failure(Exception("Empty API response"))
            
            val cleanJson = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val analysis = dreamAnalysisAdapter.fromJson(cleanJson)
                ?: return@withContext Result.failure(Exception("Failed to schema-parse dream interpretation JSON"))
            Result.success(analysis)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
