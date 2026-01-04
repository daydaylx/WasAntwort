package de.grunert.wasantwort.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.grunert.wasantwort.domain.EmojiLevel
import de.grunert.wasantwort.domain.Formality
import de.grunert.wasantwort.domain.Goal
import de.grunert.wasantwort.domain.Length
import de.grunert.wasantwort.domain.Tone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsStore(private val context: Context) {

    private object Keys {
        val API_KEY = stringPreferencesKey("api_key")
        val BASE_URL = stringPreferencesKey("base_url")
        val MODEL = stringPreferencesKey("model")
        val DEFAULT_TONE = stringPreferencesKey("default_tone")
        val DEFAULT_GOAL = stringPreferencesKey("default_goal")
        val DEFAULT_LENGTH = stringPreferencesKey("default_length")
        val DEFAULT_EMOJI_LEVEL = stringPreferencesKey("default_emoji_level")
        val DEFAULT_FORMALITY = stringPreferencesKey("default_formality")
        val USE_CONTEXT = stringPreferencesKey("use_context")
        val AUTO_DETECT_STYLE = stringPreferencesKey("auto_detect_style")
    }

    private var cachedSettings: AppSettings? = null

    private suspend fun migrateApiKeyIfNeeded() {
        if (!EncryptedKeyStore.isMigrated(context)) {
            // Try to read from old DataStore location and migrate
            // This is a one-time migration
            try {
                val prefs = context.dataStore.data.first()
                val oldApiKey = prefs[Keys.API_KEY]
                if (!oldApiKey.isNullOrBlank()) {
                    EncryptedKeyStore.setApiKey(context, oldApiKey)
                    // Remove from DataStore
                    context.dataStore.edit { it.remove(Keys.API_KEY) }
                }
                EncryptedKeyStore.markMigrated(context)
            } catch (e: Exception) {
                // Migration failed, but continue - encrypted storage is empty
                EncryptedKeyStore.markMigrated(context)
            }
        }
    }

    val apiKey: Flow<String?> = flow {
        emit(EncryptedKeyStore.getApiKey(context))
    }
    val baseUrl: Flow<String?> = context.dataStore.data.map { it[Keys.BASE_URL] }
    val model: Flow<String?> = context.dataStore.data.map { it[Keys.MODEL] }
    val defaultTone: Flow<Tone> = context.dataStore.data.map {
        it[Keys.DEFAULT_TONE]?.let { name -> Tone.valueOf(name) } ?: Tone.FREUNDLICH
    }
    val defaultGoal: Flow<Goal> = context.dataStore.data.map {
        it[Keys.DEFAULT_GOAL]?.let { name -> Goal.valueOf(name) } ?: Goal.NACHRAGEN
    }
    val defaultLength: Flow<Length> = context.dataStore.data.map {
        it[Keys.DEFAULT_LENGTH]?.let { name -> Length.valueOf(name) } ?: Length.NORMAL
    }
    val defaultEmojiLevel: Flow<EmojiLevel> = context.dataStore.data.map {
        it[Keys.DEFAULT_EMOJI_LEVEL]?.let { name -> EmojiLevel.valueOf(name) } ?: EmojiLevel.WENIG
    }
    val defaultFormality: Flow<Formality> = context.dataStore.data.map {
        it[Keys.DEFAULT_FORMALITY]?.let { name -> Formality.valueOf(name) } ?: Formality.DU
    }
    val useContext: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.USE_CONTEXT]?.toBoolean() ?: true
    }
    val autoDetectStyle: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.AUTO_DETECT_STYLE]?.toBoolean() ?: true
    }

    suspend fun setApiKey(value: String) {
        EncryptedKeyStore.setApiKey(context, value)
        invalidateCache()
    }

    suspend fun setBaseUrl(value: String) {
        context.dataStore.edit { it[Keys.BASE_URL] = value }
        invalidateCache()
    }

    suspend fun setModel(value: String) {
        context.dataStore.edit { it[Keys.MODEL] = value }
        invalidateCache()
    }

    suspend fun setDefaultTone(value: Tone) {
        context.dataStore.edit { it[Keys.DEFAULT_TONE] = value.name }
        invalidateCache()
    }

    suspend fun setDefaultGoal(value: Goal) {
        context.dataStore.edit { it[Keys.DEFAULT_GOAL] = value.name }
        invalidateCache()
    }

    suspend fun setDefaultLength(value: Length) {
        context.dataStore.edit { it[Keys.DEFAULT_LENGTH] = value.name }
        invalidateCache()
    }

    suspend fun setDefaultEmojiLevel(value: EmojiLevel) {
        context.dataStore.edit { it[Keys.DEFAULT_EMOJI_LEVEL] = value.name }
        invalidateCache()
    }

    suspend fun setDefaultFormality(value: Formality) {
        context.dataStore.edit { it[Keys.DEFAULT_FORMALITY] = value.name }
        invalidateCache()
    }

    suspend fun setUseContext(value: Boolean) {
        context.dataStore.edit { it[Keys.USE_CONTEXT] = value.toString() }
        invalidateCache()
    }

    suspend fun setAutoDetectStyle(value: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_DETECT_STYLE] = value.toString() }
        invalidateCache()
    }

    /**
     * Atomically saves all settings in a single DataStore transaction.
     * This prevents race conditions where settings could be inconsistent during partial updates.
     */
    suspend fun saveAllSettings(settings: AppSettings) {
        // Save API key to encrypted storage
        EncryptedKeyStore.setApiKey(context, settings.apiKey)

        // Save all other settings atomically in one DataStore transaction
        context.dataStore.edit { prefs ->
            prefs[Keys.BASE_URL] = settings.baseUrl
            prefs[Keys.MODEL] = settings.model
            prefs[Keys.DEFAULT_TONE] = settings.defaultTone.name
            prefs[Keys.DEFAULT_GOAL] = settings.defaultGoal.name
            prefs[Keys.DEFAULT_LENGTH] = settings.defaultLength.name
            prefs[Keys.DEFAULT_EMOJI_LEVEL] = settings.defaultEmojiLevel.name
            prefs[Keys.DEFAULT_FORMALITY] = settings.defaultFormality.name
            prefs[Keys.USE_CONTEXT] = settings.useContext.toString()
            prefs[Keys.AUTO_DETECT_STYLE] = settings.autoDetectStyle.toString()
        }

        invalidateCache()
    }

    suspend fun getCurrentSettings(): AppSettings {
        cachedSettings?.let { return it }

        // Ensure migration is done
        migrateApiKeyIfNeeded()

        val prefs = context.dataStore.data.first()
        val modelId = prefs[Keys.MODEL] ?: "meta-llama/llama-3.3-70b-instruct:free"
        val userApiKey = EncryptedKeyStore.getApiKey(context) ?: ""

        val settings = AppSettings(
            apiKey = userApiKey,
            baseUrl = prefs[Keys.BASE_URL] ?: "https://openrouter.ai/api/v1",
            model = modelId,
            defaultTone = prefs[Keys.DEFAULT_TONE]?.let { Tone.valueOf(it) } ?: Tone.FREUNDLICH,
            defaultGoal = prefs[Keys.DEFAULT_GOAL]?.let { Goal.valueOf(it) } ?: Goal.NACHRAGEN,
            defaultLength = prefs[Keys.DEFAULT_LENGTH]?.let { Length.valueOf(it) } ?: Length.NORMAL,
            defaultEmojiLevel = prefs[Keys.DEFAULT_EMOJI_LEVEL]?.let { EmojiLevel.valueOf(it) } ?: EmojiLevel.WENIG,
            defaultFormality = prefs[Keys.DEFAULT_FORMALITY]?.let { Formality.valueOf(it) } ?: Formality.DU,
            useContext = prefs[Keys.USE_CONTEXT]?.toBoolean() ?: true,
            autoDetectStyle = prefs[Keys.AUTO_DETECT_STYLE]?.toBoolean() ?: true
        )

        cachedSettings = settings
        return settings
    }

    private fun invalidateCache() {
        cachedSettings = null
    }
}

data class AppSettings(
    val apiKey: String,
    val baseUrl: String,
    val model: String,
    val defaultTone: Tone,
    val defaultGoal: Goal,
    val defaultLength: Length,
    val defaultEmojiLevel: EmojiLevel,
    val defaultFormality: Formality,
    val useContext: Boolean = true,
    val autoDetectStyle: Boolean = true
)
