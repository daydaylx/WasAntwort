package de.grunert.wasantwort.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.grunert.wasantwort.data.ApiException
import de.grunert.wasantwort.data.AppSettings
import de.grunert.wasantwort.data.Repository
import de.grunert.wasantwort.domain.ConversationEntry
import de.grunert.wasantwort.domain.EmojiLevel
import de.grunert.wasantwort.domain.Formality
import de.grunert.wasantwort.domain.Goal
import de.grunert.wasantwort.domain.Length
import de.grunert.wasantwort.domain.RewriteType
import de.grunert.wasantwort.domain.Tone
import de.grunert.wasantwort.domain.StylePreset
import de.grunert.wasantwort.domain.StyleInference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    private val _history = MutableStateFlow<List<ConversationEntry>>(emptyList())
    val history: StateFlow<List<ConversationEntry>> = _history.asStateFlow()

    init {
        loadSettings()
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            repository.getHistory().collect { entries ->
                _history.value = entries
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val settings = repository.getSettings()
                _uiState.update { state ->
                    state.copy(
                        settings = settings,
                        tone = settings.defaultTone,
                        goal = settings.defaultGoal,
                        length = settings.defaultLength,
                        emojiLevel = settings.defaultEmojiLevel,
                        formality = settings.defaultFormality
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        settings = AppSettings(
                            apiKey = "",
                            baseUrl = "https://openrouter.ai/api/v1",
                            model = "meta-llama/llama-3.3-70b-instruct:free",
                            defaultTone = Tone.FREUNDLICH,
                            defaultGoal = Goal.NACHRAGEN,
                            defaultLength = Length.NORMAL,
                            defaultEmojiLevel = EmojiLevel.WENIG,
                            defaultFormality = Formality.DU,
                            autoDetectStyle = true
                        )
                    )
                }
            }
        }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun updateTone(tone: Tone) {
        _uiState.update { it.copy(tone = tone) }
    }

    fun updateGoal(goal: Goal) {
        _uiState.update { it.copy(goal = goal) }
    }

    fun updateLength(length: Length) {
        _uiState.update { it.copy(length = length) }
    }

    fun updateEmojiLevel(emojiLevel: EmojiLevel) {
        _uiState.update { it.copy(emojiLevel = emojiLevel) }
    }

    fun updateFormality(formality: Formality) {
        _uiState.update { it.copy(formality = formality) }
    }

    fun applyPreset(preset: StylePreset) {
        _uiState.update {
            it.copy(
                tone = preset.tone,
                goal = preset.goal,
                length = preset.length,
                emojiLevel = preset.emojiLevel,
                formality = preset.formality
            )
        }
    }

    fun generateSuggestions() {
        val currentState = _uiState.value
        val inputText = currentState.inputText.trim()

        val settings = currentState.settings
        if (settings == null || settings.apiKey.isBlank() || settings.baseUrl.isBlank()) {
            _uiState.update {
                it.copy(uiState = MainUiState.Error("Bitte zuerst API-Einstellungen konfigurieren.", ErrorSource.VALIDATION))
            }
            return
        }

        if (inputText.isBlank()) {
            _uiState.update {
                it.copy(uiState = MainUiState.Error("Bitte zuerst eine Nachricht eingeben.", ErrorSource.VALIDATION))
            }
            return
        }

        if (inputText.length > 4000) {
            _uiState.update {
                it.copy(uiState = MainUiState.Error("Nachricht zu lang (max. 4000 Zeichen).", ErrorSource.VALIDATION))
            }
            return
        }

        val inferredStyle = if (settings.autoDetectStyle) {
            StyleInference.infer(inputText)
        } else {
            null
        }

        val toneToUse = inferredStyle?.tone ?: currentState.tone
        val formalityToUse = inferredStyle?.formality ?: currentState.formality

        _uiState.update {
            it.copy(
                tone = toneToUse,
                formality = formalityToUse,
                uiState = MainUiState.Loading,
                suggestions = emptyList(),
                selectedSuggestionIndex = null
            )
        }

        viewModelScope.launch {
            val result = repository.generateSuggestions(
                originalMessage = inputText,
                tone = toneToUse,
                goal = currentState.goal,
                length = currentState.length,
                emojiLevel = currentState.emojiLevel,
                formality = formalityToUse
            )

            result.fold(
                onSuccess = { suggestions ->
                    _uiState.update {
                        it.copy(
                            uiState = MainUiState.Success(suggestions),
                            suggestions = suggestions
                        )
                    }
                },
                onFailure = { error ->
                    val errorMessage = when (error) {
                        is ApiException -> error.message
                        else -> "Fehler: ${error.message}"
                    }
                    _uiState.update {
                        it.copy(uiState = MainUiState.Error(errorMessage ?: "Unbekannter Fehler", ErrorSource.GENERATE))
                    }
                }
            )
        }
    }

    fun rewriteSuggestion(index: Int, rewriteType: RewriteType) {
        val currentState = _uiState.value
        val suggestion = currentState.suggestions.getOrNull(index)
            ?: return

        val settings = currentState.settings
        if (settings == null || settings.apiKey.isBlank() || settings.baseUrl.isBlank()) {
            _uiState.update {
                it.copy(uiState = MainUiState.Error("Bitte zuerst API-Einstellungen konfigurieren.", ErrorSource.VALIDATION))
            }
            return
        }

        _uiState.update {
            it.copy(uiState = MainUiState.Loading)
        }

        viewModelScope.launch {
            val result = repository.rewriteSuggestion(
                originalMessage = currentState.inputText.takeIf { it.isNotBlank() },
                selectedSuggestion = suggestion,
                rewriteType = rewriteType
            )

            result.fold(
                onSuccess = { rewritten ->
                    val updatedSuggestions = currentState.suggestions.toMutableList()
                    updatedSuggestions[index] = rewritten
                    _uiState.update {
                        it.copy(
                            uiState = MainUiState.Success(updatedSuggestions),
                            suggestions = updatedSuggestions
                        )
                    }
                },
                onFailure = { error ->
                    val errorMessage = when (error) {
                        is ApiException -> error.message
                        else -> "Fehler: ${error.message}"
                    }
                    _uiState.update {
                        it.copy(uiState = MainUiState.Error(errorMessage ?: "Unbekannter Fehler", ErrorSource.REWRITE))
                    }
                }
            )
        }
    }

    fun clearInput() {
        _uiState.update { it.copy(inputText = "") }
    }

    fun clearError() {
        val currentState = _uiState.value
        if (currentState.uiState is MainUiState.Error) {
            _uiState.update {
                val fallbackState = if (it.suggestions.isNotEmpty()) {
                    MainUiState.Success(it.suggestions)
                } else {
                    MainUiState.Idle
                }
                it.copy(uiState = fallbackState)
            }
        }
    }

    fun saveSettings(settings: AppSettings) {
        viewModelScope.launch {
            try {
                repository.saveSettings(settings)
                _uiState.update { it.copy(settings = settings) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(uiState = MainUiState.Error("Fehler beim Speichern der Einstellungen", ErrorSource.SETTINGS))
                }
            }
        }
    }

    fun loadFromHistory(entry: ConversationEntry) {
        _uiState.update {
            it.copy(
                inputText = entry.inputText,
                tone = entry.tone,
                goal = entry.goal,
                length = entry.length,
                emojiLevel = entry.emojiLevel,
                formality = entry.formality
            )
        }
    }

    fun deleteHistoryEntry(entryId: String) {
        viewModelScope.launch {
            try {
                repository.deleteHistoryEntry(entryId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(uiState = MainUiState.Error("Fehler beim Löschen", ErrorSource.HISTORY))
                }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                repository.clearHistory()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(uiState = MainUiState.Error("Fehler beim Löschen der Historie", ErrorSource.HISTORY))
                }
            }
        }
    }
}
