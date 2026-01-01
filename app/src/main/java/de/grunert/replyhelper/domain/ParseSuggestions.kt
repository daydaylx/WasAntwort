package de.grunert.replyhelper.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class SuggestionsResponse(
    val suggestions: List<String>
)

@Serializable
data class RewriteResponse(
    val text: String
)

object ParseSuggestions {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Parse API response to exactly 5 suggestions.
     * Falls back to heuristic parsing if JSON is malformed.
     */
    fun parseSuggestionsResponse(responseText: String): List<String> {
        return try {
            // Try parsing as JSON first
            val jsonObject = json.parseToJsonElement(responseText).jsonObject
            
            // Try "suggestions" array
            val suggestions = jsonObject["suggestions"]?.jsonArray
                ?.mapNotNull { it.jsonPrimitive.content }
            
            if (suggestions != null && suggestions.size >= 5) {
                return suggestions.take(5)
            }
            
            // Try "choices" array (OpenAI format)
            val choices = jsonObject["choices"]?.jsonArray
                ?.mapNotNull { 
                    it.jsonObject["message"]?.jsonObject?.get("content")?.jsonPrimitive?.content
                }
            
            if (choices != null && choices.size >= 5) {
                return choices.take(5)
            }
            
            // Fallback to heuristic parsing
            parseHeuristic(responseText)
        } catch (e: Exception) {
            // JSON parsing failed, use heuristic
            parseHeuristic(responseText)
        }
    }

    /**
     * Heuristic parsing: extract text blocks and pad to 5 suggestions.
     */
    private fun parseHeuristic(text: String): List<String> {
        // Remove markdown code blocks if present
        var cleaned = text.replace(Regex("```(json)?\\s*"), "")
            .replace(Regex("```\\s*"), "")
            .trim()

        // Try to extract JSON array content
        val arrayMatch = Regex("""\[(.*)\]""", RegexOption.DOT_MATCHES_ALL).find(cleaned)
        if (arrayMatch != null) {
            cleaned = arrayMatch.groupValues[1]
        }

        // Split by common delimiters
        val candidates = cleaned.split(Regex("""["\n]+"""))
            .map { it.trim() }
            .filter { it.isNotBlank() && it.length > 3 }
            .distinct()

        val suggestions = when {
            candidates.isEmpty() -> listOf("Vielen Dank!", "Ok, passt.", "Super!", "Alles klar.", "Danke für die Info!")
            candidates.size == 1 -> {
                // Duplicate and vary the single suggestion
                val base = candidates.first()
                listOf(
                    base,
                    base.take(base.length.coerceAtMost(50)) + "...",
                    "Ok.",
                    "Danke!",
                    "Alles klar."
                )
            }
            candidates.size < 5 -> {
                // Pad with variations
                candidates + generatePadding(candidates.lastOrNull() ?: "Ok", 5 - candidates.size)
            }
            else -> candidates.take(5)
        }

        return suggestions.map { it.trim() }.filter { it.isNotBlank() }.take(5)
    }

    private fun generatePadding(baseText: String, count: Int): List<String> {
        val padding = listOf("Ok.", "Alles klar.", "Danke!", "Super!", "Passt.")
        return padding.take(count)
    }

    /**
     * Parse rewrite response to single text.
     */
    fun parseRewriteResponse(responseText: String): String {
        return try {
            val jsonObject = json.parseToJsonElement(responseText).jsonObject
            
            // Try "text" field
            jsonObject["text"]?.jsonPrimitive?.content
                ?: jsonObject["suggestion"]?.jsonPrimitive?.content
                ?: jsonObject["content"]?.jsonPrimitive?.content
                ?: parseHeuristic(responseText).firstOrNull()
                ?: responseText.trim()
        } catch (e: Exception) {
            // Remove JSON artifacts and return cleaned text
            responseText.replace(Regex(""""\s*(text|suggestion|content)\s*"\s*:\s*"""), "")
                .replace(Regex("""[{}"]"""), "")
                .trim()
                .ifBlank { responseText.trim() }
        }
    }
}


