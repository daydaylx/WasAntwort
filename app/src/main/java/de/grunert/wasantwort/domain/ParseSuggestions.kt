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

enum class ParseSource {
    DIRECT_JSON,
    NESTED_JSON,
    CHOICES,
    HEURISTIC
}

data class SuggestionsParseResult(
    val suggestions: List<String>,
    val source: ParseSource
)

object ParseSuggestions {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parseSuggestionsResponse(responseText: String): List<String> {
        return parseSuggestionsResponseDetailed(responseText).suggestions
    }

    fun parseSuggestionsResponseDetailed(responseText: String): SuggestionsParseResult {
        return try {
            val cleanedText = stripCodeFences(responseText)
            val jsonObject = json.parseToJsonElement(cleanedText).jsonObject

            val suggestions = jsonObject["suggestions"]?.jsonArray
                ?.mapNotNull { it.jsonPrimitive.content }
                ?.filter { it.isNotBlank() }

            if (!suggestions.isNullOrEmpty()) {
                val normalized = normalizeSuggestions(suggestions)
                val source = if (suggestions.size >= 5) ParseSource.DIRECT_JSON else ParseSource.HEURISTIC
                return SuggestionsParseResult(normalized, source)
            }

            val choices = jsonObject["choices"]?.jsonArray

            val nestedContent = choices
                ?.firstOrNull()
                ?.jsonObject
                ?.get("message")
                ?.jsonObject
                ?.get("content")
                ?.jsonPrimitive
                ?.content

            if (!nestedContent.isNullOrBlank()) {
                val nestedSuggestions = parseNestedSuggestions(nestedContent)
                if (!nestedSuggestions.isNullOrEmpty()) {
                    val normalized = normalizeSuggestions(nestedSuggestions)
                    val source = if (nestedSuggestions.size >= 5) ParseSource.NESTED_JSON else ParseSource.HEURISTIC
                    return SuggestionsParseResult(normalized, source)
                }
            }

            val choiceSuggestions = choices
                ?.mapNotNull {
                    it.jsonObject["message"]?.jsonObject?.get("content")?.jsonPrimitive?.content
                }
                ?.filter { it.isNotBlank() }

            if (!choiceSuggestions.isNullOrEmpty()) {
                val normalized = normalizeSuggestions(choiceSuggestions)
                val source = if (choiceSuggestions.size >= 5) ParseSource.CHOICES else ParseSource.HEURISTIC
                return SuggestionsParseResult(normalized, source)
            }

            SuggestionsParseResult(parseHeuristic(responseText), ParseSource.HEURISTIC)
        } catch (e: Exception) {
            SuggestionsParseResult(parseHeuristic(responseText), ParseSource.HEURISTIC)
        }
    }

    private fun parseHeuristic(text: String): List<String> {
        var cleaned = stripCodeFences(text)

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
                candidates + generatePadding(5 - candidates.size)
            }
            else -> candidates.take(5)
        }

        return normalizeSuggestions(suggestions)
    }

    private fun generatePadding(count: Int): List<String> {
        val padding = listOf("Ok.", "Alles klar.", "Danke!", "Super!", "Passt.")
        return padding.take(count)
    }

    private fun normalizeSuggestions(raw: List<String>): List<String> {
        val cleaned = raw
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()

        return if (cleaned.size < 5) {
            cleaned + generatePadding(5 - cleaned.size)
        } else {
            cleaned.take(5)
        }
    }

    private fun parseNestedSuggestions(content: String): List<String>? {
        return try {
            val cleaned = stripCodeFences(content)
            val nested = json.parseToJsonElement(cleaned).jsonObject
            nested["suggestions"]?.jsonArray
                ?.mapNotNull { it.jsonPrimitive.content }
                ?.filter { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    private fun stripCodeFences(text: String): String {
        return text.replace(Regex("```(json)?\\s*"), "")
            .replace(Regex("```\\s*"), "")
            .trim()
    }

    fun parseRewriteResponse(responseText: String): String {
        val fallback = parseHeuristic(responseText).firstOrNull()?.trim()
        val trimmed = responseText.trim()

        return try {
            val cleanedText = stripCodeFences(responseText)
            val jsonObject = json.parseToJsonElement(cleanedText).jsonObject

            val direct = jsonObject["text"]?.jsonPrimitive?.content
                ?: jsonObject["suggestion"]?.jsonPrimitive?.content
                ?: jsonObject["content"]?.jsonPrimitive?.content

            if (!direct.isNullOrBlank()) {
                return direct.trim()
            }

            val choiceContent = jsonObject["choices"]?.jsonArray
                ?.firstOrNull()
                ?.jsonObject
                ?.get("message")
                ?.jsonObject
                ?.get("content")
                ?.jsonPrimitive
                ?.content

            if (!choiceContent.isNullOrBlank()) {
                val nestedClean = stripCodeFences(choiceContent)
                val nestedText = try {
                    val nestedObject = json.parseToJsonElement(nestedClean).jsonObject
                    nestedObject["text"]?.jsonPrimitive?.content
                        ?: nestedObject["suggestion"]?.jsonPrimitive?.content
                        ?: nestedObject["content"]?.jsonPrimitive?.content
                } catch (e: Exception) {
                    null
                }

                return nestedText?.takeIf { it.isNotBlank() } ?: choiceContent.trim()
            }

            fallback ?: trimmed
        } catch (e: Exception) {
            fallback
                ?: trimmed.ifBlank { "Ok." }
        }
    }
}
