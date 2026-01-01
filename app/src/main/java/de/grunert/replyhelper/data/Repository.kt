package de.grunert.replyhelper.data

import de.grunert.replyhelper.domain.EmojiLevel
import de.grunert.replyhelper.domain.Formality
import de.grunert.replyhelper.domain.Goal
import de.grunert.replyhelper.domain.Length
import de.grunert.replyhelper.domain.ParseSuggestions
import de.grunert.replyhelper.domain.PromptBuilder
import de.grunert.replyhelper.domain.RewriteType
import de.grunert.replyhelper.domain.Tone

class Repository(
    private val settingsStore: SettingsStore
) {

    suspend fun generateSuggestions(
        originalMessage: String,
        tone: Tone,
        goal: Goal,
        length: Length,
        emojiLevel: EmojiLevel,
        formality: Formality
    ): Result<List<String>> {
        val settings = settingsStore.getCurrentSettings()

        if (settings.apiKey.isBlank()) {
            return Result.failure(ApiException("API-Key fehlt. Bitte in den Einstellungen konfigurieren."))
        }

        val systemPrompt = PromptBuilder.getSystemPrompt()
        val userPrompt = PromptBuilder.buildGeneratePrompt(
            originalMessage = originalMessage,
            tone = tone,
            goal = goal,
            length = length,
            emojiLevel = emojiLevel,
            formality = formality
        )

        val client = AiClient(settings.baseUrl, settings.apiKey)
        return try {
            client.generateSuggestions(
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
                model = settings.model
            )
        } finally {
            client.close()
        }
    }

    suspend fun rewriteSuggestion(
        originalMessage: String?,
        selectedSuggestion: String,
        rewriteType: RewriteType
    ): Result<String> {
        val settings = settingsStore.getCurrentSettings()

        if (settings.apiKey.isBlank()) {
            return Result.failure(ApiException("API-Key fehlt. Bitte in den Einstellungen konfigurieren."))
        }

        val systemPrompt = PromptBuilder.getSystemPrompt()
        val userPrompt = PromptBuilder.buildRewritePrompt(
            originalMessage = originalMessage,
            selectedSuggestion = selectedSuggestion,
            rewriteType = rewriteType
        )

        val client = AiClient(settings.baseUrl, settings.apiKey)
        return try {
            client.rewriteSuggestion(
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
                model = settings.model
            )
        } finally {
            client.close()
        }
    }

    suspend fun getSettings(): AppSettings {
        return settingsStore.getCurrentSettings()
    }

    suspend fun saveSettings(settings: AppSettings) {
        settingsStore.setApiKey(settings.apiKey)
        settingsStore.setBaseUrl(settings.baseUrl)
        settingsStore.setModel(settings.model)
        settingsStore.setDefaultTone(settings.defaultTone)
        settingsStore.setDefaultGoal(settings.defaultGoal)
        settingsStore.setDefaultLength(settings.defaultLength)
        settingsStore.setDefaultEmojiLevel(settings.defaultEmojiLevel)
        settingsStore.setDefaultFormality(settings.defaultFormality)
    }
}


