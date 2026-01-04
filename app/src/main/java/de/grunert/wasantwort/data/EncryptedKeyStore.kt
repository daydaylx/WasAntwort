package de.grunert.wasantwort.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Helper object for encrypted storage of sensitive data (API keys)
 */
object EncryptedKeyStore {
    private const val PREFS_NAME = "encrypted_api_key_prefs"
    private const val KEY_API_KEY = "api_key"
    private const val KEY_MIGRATED = "key_migrated"

    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Stores the API key in encrypted shared preferences
     */
    fun setApiKey(context: Context, apiKey: String) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    /**
     * Retrieves the API key from encrypted shared preferences
     */
    fun getApiKey(context: Context): String? {
        val prefs = getEncryptedPrefs(context)
        return prefs.getString(KEY_API_KEY, null)
    }

    /**
     * Clears the encrypted API key
     */
    fun clearApiKey(context: Context) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit().remove(KEY_API_KEY).apply()
    }

    /**
     * Marks that migration from unencrypted storage has been completed
     */
    fun markMigrated(context: Context) {
        val prefs = getEncryptedPrefs(context)
        prefs.edit().putBoolean(KEY_MIGRATED, true).apply()
    }

    /**
     * Checks if migration has been completed
     */
    fun isMigrated(context: Context): Boolean {
        val prefs = getEncryptedPrefs(context)
        return prefs.getBoolean(KEY_MIGRATED, false)
    }
}

