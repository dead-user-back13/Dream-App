package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.DreamDatabase
import com.example.data.database.DreamEntity
import com.example.data.model.DreamAnalysis
import com.example.data.repository.DreamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DreamViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("dream_prefs", Context.MODE_PRIVATE)
    private val database = DreamDatabase.getDatabase(application)
    private val repository = DreamRepository(database.dreamDao())

    val historyDreams: StateFlow<List<DreamEntity>> = repository.allDreams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedDreams: StateFlow<List<DreamEntity>> = repository.bookmarkedDreams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI States
    private val _userDreamInput = MutableStateFlow("")
    val userDreamInput = _userDreamInput.asStateFlow()

    private val _isInterpretationLoading = MutableStateFlow(false)
    val isInterpretationLoading = _isInterpretationLoading.asStateFlow()

    private val _interpretationError = MutableStateFlow<String?>(null)
    val interpretationError = _interpretationError.asStateFlow()

    // Active displayed interpretation (either newly fetched, or loaded from history)
    private val _currentAnalysis = MutableStateFlow<DreamAnalysis?>(null)
    val currentAnalysis = _currentAnalysis.asStateFlow()

    // In case we loaded/selected a dream from history, track its ID so we can toggle bookmark directly
    private val _currentDreamId = MutableStateFlow<Long?>(null)
    val currentDreamId = _currentDreamId.asStateFlow()

    private val _isCurrentDreamBookmarked = MutableStateFlow(false)
    val isCurrentDreamBookmarked = _isCurrentDreamBookmarked.asStateFlow()

    // Config preferences
    private val _appLanguage = MutableStateFlow(sharedPrefs.getString("app_lang", "fa") ?: "fa")
    val appLanguage = _appLanguage.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(sharedPrefs.getBoolean("is_dark_theme", true))
    val isDarkTheme = _isDarkTheme.asStateFlow()

    private val _customApiKey = MutableStateFlow(sharedPrefs.getString("api_key", "") ?: "")
    val customApiKey = _customApiKey.asStateFlow()

    private val _activeTab = MutableStateFlow("interpret") // "interpret", "history", "bookmarks"
    val activeTab = _activeTab.asStateFlow()

    init {
        // Observe current dream history item bookmark status to keep state in sync
        viewModelScope.launch {
            _currentDreamId.collectLatest { id ->
                if (id != null) {
                    repository.getDreamById(id).collectLatest { entity ->
                        _isCurrentDreamBookmarked.value = entity?.isBookmarked ?: false
                    }
                } else {
                    _isCurrentDreamBookmarked.value = false
                }
            }
        }
    }

    fun onDreamInputChanged(input: String) {
        _userDreamInput.value = input
    }

    fun setAppLanguage(lang: String) {
        _appLanguage.value = lang
        sharedPrefs.edit().putString("app_lang", lang).apply()
    }

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        sharedPrefs.edit().putBoolean("is_dark_theme", isDark).apply()
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
        sharedPrefs.edit().putString("api_key", key).apply()
    }

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    fun selectHistoryDream(entity: DreamEntity) {
        val analysis = DreamAnalysis(
            titleFa = entity.titleFa,
            titleEn = entity.titleEn,
            interpretationFa = entity.interpretationFa,
            interpretationEn = entity.interpretationEn,
            mood = entity.mood,
            colorHex = entity.colorHex,
            symbols = repository.parseSymbols(entity.symbolsJson)
        )
        _currentAnalysis.value = analysis
        _currentDreamId.value = entity.id
        _userDreamInput.value = entity.userPrompt
        _activeTab.value = "interpret"
    }

    fun clearCurrentAnalysis() {
        _currentAnalysis.value = null
        _currentDreamId.value = null
        _userDreamInput.value = ""
    }

    fun toggleBookmarkCurrentDream() {
        val dreamId = _currentDreamId.value
        val analysis = _currentAnalysis.value
        val prompt = _userDreamInput.value

        if (dreamId != null) {
            viewModelScope.launch {
                val nextBookmarkState = !_isCurrentDreamBookmarked.value
                repository.updateBookmarkStatus(dreamId, nextBookmarkState)
                _isCurrentDreamBookmarked.value = nextBookmarkState
            }
        } else if (analysis != null) {
            // Save a brand new analysis with bookmarked = true
            viewModelScope.launch {
                val nextBookmarkState = true
                val newId = repository.saveDreamAnalysis(prompt, analysis, nextBookmarkState)
                _currentDreamId.value = newId
                _isCurrentDreamBookmarked.value = nextBookmarkState
            }
        }
    }

    fun deleteDream(entity: DreamEntity) {
        viewModelScope.launch {
            if (_currentDreamId.value == entity.id) {
                _currentDreamId.value = null
                _currentAnalysis.value = null
            }
            repository.deleteDream(entity)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _currentDreamId.value = null
            _currentAnalysis.value = null
        }
    }

    fun interpretUserDream() {
        val prompt = _userDreamInput.value.trim()
        if (prompt.isBlank()) return

        _isInterpretationLoading.value = true
        _interpretationError.value = null
        _currentAnalysis.value = null
        _currentDreamId.value = null

        viewModelScope.launch {
            val key = _customApiKey.value.trim().takeIf { it.isNotBlank() }
            val result = repository.interpretDream(prompt, key)
            _isInterpretationLoading.value = false

            result.onSuccess { analysis ->
                _currentAnalysis.value = analysis
                // Save to historical database implicitly
                val newId = repository.saveDreamAnalysis(prompt, analysis, false)
                _currentDreamId.value = newId
            }.onFailure { error ->
                _interpretationError.value = error.message ?: "Unknown error"
            }
        }
    }
}
