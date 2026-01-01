package de.grunert.wasantwort.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.grunert.wasantwort.domain.ConversationEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.historyDataStore: DataStore<Preferences> by preferencesDataStore(name = "conversation_history")

class HistoryStore(private val context: Context) {

    private object Keys {
        val HISTORY_JSON = stringPreferencesKey("history_json")
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    companion object {
        private const val MAX_HISTORY_ENTRIES = 100
    }

    val history: Flow<List<ConversationEntry>> = context.historyDataStore.data.map { prefs ->
        val jsonString = prefs[Keys.HISTORY_JSON] ?: "[]"
        try {
            json.decodeFromString<List<ConversationEntry>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addEntry(entry: ConversationEntry) {
        context.historyDataStore.edit { prefs ->
            val currentHistory = try {
                val jsonString = prefs[Keys.HISTORY_JSON] ?: "[]"
                json.decodeFromString<List<ConversationEntry>>(jsonString)
            } catch (e: Exception) {
                emptyList()
            }

            val updatedHistory = (listOf(entry) + currentHistory)
                .take(MAX_HISTORY_ENTRIES)

            prefs[Keys.HISTORY_JSON] = json.encodeToString(updatedHistory)
        }
    }

    suspend fun deleteEntry(entryId: String) {
        context.historyDataStore.edit { prefs ->
            val currentHistory = try {
                val jsonString = prefs[Keys.HISTORY_JSON] ?: "[]"
                json.decodeFromString<List<ConversationEntry>>(jsonString)
            } catch (e: Exception) {
                emptyList()
            }

            val updatedHistory = currentHistory.filter { it.id != entryId }
            prefs[Keys.HISTORY_JSON] = json.encodeToString(updatedHistory)
        }
    }

    suspend fun clearHistory() {
        context.historyDataStore.edit { prefs ->
            prefs[Keys.HISTORY_JSON] = "[]"
        }
    }

    suspend fun getRecentEntries(count: Int): List<ConversationEntry> {
        val allHistory = history.first()
        return allHistory.take(count)
    }
}
