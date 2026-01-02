package de.grunert.wasantwort.data

import de.grunert.wasantwort.domain.ConversationEntry
import de.grunert.wasantwort.domain.EmojiLevel
import de.grunert.wasantwort.domain.Formality
import de.grunert.wasantwort.domain.Goal
import de.grunert.wasantwort.domain.Length
import de.grunert.wasantwort.domain.PromptBuilder
import de.grunert.wasantwort.domain.RewriteType
import de.grunert.wasantwort.domain.Tone
import java.util.UUID

class Repository(
    private val settingsStore: SettingsStore,
    private val historyStore: HistoryStore
) {
    private var cachedClient: Pair<String, AiClient>? = null

    private fun getClient(baseUrl: String, apiKey: String): AiClient {
        val key = "$baseUrl::$apiKey"
        val cached = cachedClient

        if (cached?.first == key) {
            return cached.second
        }

        cached?.second?.close()

        val newClient = AiClient(baseUrl, apiKey)
        cachedClient = key to newClient
        return newClient
    }

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

        // Build context messages from recent history
        val contextMessages = if (settings.useContext) {
            buildContextMessages(limit = 5)
        } else {
            emptyList()
        }

        val client = getClient(settings.baseUrl, settings.apiKey)
        val result = client.generateSuggestions(
            systemPrompt = systemPrompt,
            userPrompt = userPrompt,
            model = settings.model,
            contextMessages = contextMessages
        )

        // Save to history on success
        result.onSuccess { suggestions ->
            val entry = ConversationEntry(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                inputText = originalMessage,
                tone = tone,
                goal = goal,
                length = length,
                emojiLevel = emojiLevel,
                formality = formality,
                suggestions = suggestions
            )
            historyStore.addEntry(entry)
        }

        return result
    }

    private suspend fun buildContextMessages(limit: Int): List<ChatMessage> {
        val recentEntries = historyStore.getRecentEntries(limit)
        return recentEntries.reversed().flatMap { entry ->
            listOf(
                ChatMessage(role = "user", content = entry.inputText),
                ChatMessage(role = "assistant", content = entry.suggestions.firstOrNull() ?: "")
            )
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

        val client = getClient(settings.baseUrl, settings.apiKey)
        return client.rewriteSuggestion(
            systemPrompt = systemPrompt,
            userPrompt = userPrompt,
            model = settings.model
        )
    }

    suspend fun getSettings(): AppSettings {
        return settingsStore.getCurrentSettings()
    }

    suspend fun saveSettings(settings: AppSettings) {
        val oldSettings = settingsStore.getCurrentSettings()

        settingsStore.setApiKey(settings.apiKey)
        settingsStore.setBaseUrl(settings.baseUrl)
        settingsStore.setModel(settings.model)
        settingsStore.setDefaultTone(settings.defaultTone)
        settingsStore.setDefaultGoal(settings.defaultGoal)
        settingsStore.setDefaultLength(settings.defaultLength)
        settingsStore.setDefaultEmojiLevel(settings.defaultEmojiLevel)
        settingsStore.setDefaultFormality(settings.defaultFormality)
        settingsStore.setUseContext(settings.useContext)
        settingsStore.setAutoDetectStyle(settings.autoDetectStyle)

        if (oldSettings.apiKey != settings.apiKey || oldSettings.baseUrl != settings.baseUrl) {
            cachedClient?.second?.close()
            cachedClient = null
        }
    }

    suspend fun getHistory() = historyStore.history

    suspend fun deleteHistoryEntry(entryId: String) {
        historyStore.deleteEntry(entryId)
    }

    suspend fun clearHistory() {
        historyStore.clearHistory()
    }

    fun cleanup() {
        cachedClient?.second?.close()
        cachedClient = null
    }
}
