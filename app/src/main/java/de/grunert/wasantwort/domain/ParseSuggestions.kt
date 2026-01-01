package de.grunert.wasantwort.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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

    fun parseSuggestionsResponse(responseText: String): List<String> {
        return try {
            val jsonObject = json.parseToJsonElement(responseText).jsonObject

            val suggestions = jsonObject["suggestions"]?.jsonArray
                ?.mapNotNull { it.jsonPrimitive.content }

            if (suggestions != null && suggestions.size >= 5) {
                return suggestions.take(5)
            }

            val choices = jsonObject["choices"]?.jsonArray
                ?.mapNotNull {
                    it.jsonObject["message"]?.jsonObject?.get("content")?.jsonPrimitive?.content
                }

            if (choices != null && choices.size >= 5) {
                return choices.take(5)
            }

            parseHeuristic(responseText)
        } catch (e: Exception) {
            parseHeuristic(responseText)
        }
    }

    private fun parseHeuristic(text: String): List<String> {
        var cleaned = text.replace(Regex("```(json)?\\s*"), "")
            .replace(Regex("```\\s*"), "")
            .trim()

        val arrayMatch = Regex("""\[(.*)\]""", RegexOption.DOT_MATCHES_ALL).find(cleaned)
        if (arrayMatch != null) {
            cleaned = arrayMatch.groupValues[1]
        }

        val candidates = cleaned.split(Regex("""["\n]+"""))
            .map { it.trim() }
            .filter { it.isNotBlank() && it.length > 3 }
            .distinct()

        val suggestions = when {
            candidates.isEmpty() -> {
                val firstLine = text.lines()
                    .firstOrNull { it.trim().isNotBlank() }
                    ?.take(100)
                    ?.trim()
                    ?: "Ok"

                listOf(
                    firstLine,
                    "Alles klar.",
                    "Danke!",
                    "Passt.",
                    "Verstanden."
                )
            }
            candidates.size == 1 -> {
                val base = candidates.first()
                listOf(
                    base,
                    base.take(base.length.coerceAtMost(50)) + if (base.length > 50) "..." else "",
                    "Ok.",
                    "Danke!",
                    "Alles klar."
                )
            }
            candidates.size < 5 -> {
                candidates + generatePadding(candidates.lastOrNull() ?: "Ok", 5 - candidates.size)
            }
            else -> candidates.take(5)
        }

        return suggestions
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .take(5)
            .let { list ->
                if (list.size < 5) {
                    list + generatePadding("Ok", 5 - list.size)
                } else {
                    list
                }
            }
    }

    private fun generatePadding(baseText: String, count: Int): List<String> {
        val padding = listOf("Ok.", "Alles klar.", "Danke!", "Super!", "Passt.")
        return padding.take(count)
    }

    fun parseRewriteResponse(responseText: String): String {
        return try {
            val jsonObject = json.parseToJsonElement(responseText).jsonObject

            jsonObject["text"]?.jsonPrimitive?.content
                ?: jsonObject["suggestion"]?.jsonPrimitive?.content
                ?: jsonObject["content"]?.jsonPrimitive?.content
                ?: parseHeuristic(responseText).firstOrNull()
                ?: responseText.trim()
        } catch (e: Exception) {
            responseText.replace(Regex(""""\s*(text|suggestion|content)\s*"\s*:\s*"""), "")
                .replace(Regex("""[{}"]"""), "")
                .trim()
                .ifBlank { responseText.trim() }
        }
    }
}
