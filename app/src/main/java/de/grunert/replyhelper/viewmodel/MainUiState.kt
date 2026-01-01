package de.grunert.replyhelper.viewmodel

import de.grunert.replyhelper.data.AppSettings
import de.grunert.replyhelper.domain.EmojiLevel
import de.grunert.replyhelper.domain.Formality
import de.grunert.replyhelper.domain.Goal
import de.grunert.replyhelper.domain.Length
import de.grunert.replyhelper.domain.Tone

sealed class MainUiState {
    object Idle : MainUiState()
    object Loading : MainUiState()
    data class Success(val suggestions: List<String>) : MainUiState()
    data class Error(val message: String) : MainUiState()
}

data class MainScreenState(
    val inputText: String = "",
    val tone: Tone = Tone.FREUNDLICH,
    val goal: Goal = Goal.NACHRAGEN,
    val length: Length = Length.NORMAL,
    val emojiLevel: EmojiLevel = EmojiLevel.WENIG,
    val formality: Formality = Formality.DU,
    val suggestions: List<String> = emptyList(),
    val selectedSuggestionIndex: Int? = null,
    val uiState: MainUiState = MainUiState.Idle,
    val settings: AppSettings? = null
)


