package de.grunert.replyhelper.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.grunert.replyhelper.domain.EmojiLevel
import de.grunert.replyhelper.domain.Formality
import de.grunert.replyhelper.domain.Goal
import de.grunert.replyhelper.domain.Length
import de.grunert.replyhelper.domain.Tone
import kotlinx.coroutines.flow.Flow
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
    }

    val apiKey: Flow<String?> = context.dataStore.data.map { it[Keys.API_KEY] }
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

    suspend fun setApiKey(value: String) {
        context.dataStore.edit { it[Keys.API_KEY] = value }
    }

    suspend fun setBaseUrl(value: String) {
        context.dataStore.edit { it[Keys.BASE_URL] = value }
    }

    suspend fun setModel(value: String) {
        context.dataStore.edit { it[Keys.MODEL] = value }
    }

    suspend fun setDefaultTone(value: Tone) {
        context.dataStore.edit { it[Keys.DEFAULT_TONE] = value.name }
    }

    suspend fun setDefaultGoal(value: Goal) {
        context.dataStore.edit { it[Keys.DEFAULT_GOAL] = value.name }
    }

    suspend fun setDefaultLength(value: Length) {
        context.dataStore.edit { it[Keys.DEFAULT_LENGTH] = value.name }
    }

    suspend fun setDefaultEmojiLevel(value: EmojiLevel) {
        context.dataStore.edit { it[Keys.DEFAULT_EMOJI_LEVEL] = value.name }
    }

    suspend fun setDefaultFormality(value: Formality) {
        context.dataStore.edit { it[Keys.DEFAULT_FORMALITY] = value.name }
    }

    suspend fun getCurrentSettings(): AppSettings {
        val prefs = context.dataStore.data.first()
        return AppSettings(
            apiKey = prefs[Keys.API_KEY] ?: "",
            baseUrl = prefs[Keys.BASE_URL] ?: "https://api.openai.com/v1",
            model = prefs[Keys.MODEL] ?: "gpt-3.5-turbo",
            defaultTone = prefs[Keys.DEFAULT_TONE]?.let { Tone.valueOf(it) } ?: Tone.FREUNDLICH,
            defaultGoal = prefs[Keys.DEFAULT_GOAL]?.let { Goal.valueOf(it) } ?: Goal.NACHRAGEN,
            defaultLength = prefs[Keys.DEFAULT_LENGTH]?.let { Length.valueOf(it) } ?: Length.NORMAL,
            defaultEmojiLevel = prefs[Keys.DEFAULT_EMOJI_LEVEL]?.let { EmojiLevel.valueOf(it) } ?: EmojiLevel.WENIG,
            defaultFormality = prefs[Keys.DEFAULT_FORMALITY]?.let { Formality.valueOf(it) } ?: Formality.DU
        )
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
    val defaultFormality: Formality
)

