package de.grunert.wasantwort.data
import de.grunert.wasantwort.BuildConfig
import de.grunert.wasantwort.domain.ParseSource
import de.grunert.wasantwort.domain.ParseSuggestions
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.UnknownHostException

class AiClient(private val baseUrl: String, private val apiKey: String) {

    // Ensure base URL ends with trailing slash for proper path resolution
    private val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 10_000
            requestTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }
        // Logging: NONE for release, HEADERS for debug (avoids logging sensitive request/response bodies)
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    if (BuildConfig.DEBUG) {
                        android.util.Log.d("AiClient", message)
                    }
                }
            }
            level = if (BuildConfig.DEBUG) LogLevel.HEADERS else LogLevel.NONE
        }
        defaultRequest {
            url(normalizedBaseUrl)
        }
    }
    // Re-configuring timeouts in init block or using install(HttpTimeout) { ... } is better.

    private suspend fun validateJsonResponse(response: HttpResponse): Result<ChatCompletionResponse> {
        val contentType = response.contentType()
        return if (contentType?.match(ContentType.Application.Json) == true) {
            try {
                Result.success(response.body())
            } catch (e: Exception) {
                Result.failure(ApiException("Fehler beim Parsen der Antwort: ${e.message}"))
            }
        } else {
            val bodyText = response.bodyAsText().take(200)
            Result.failure(ApiException("API-Fehler: HTML statt JSON erhalten. Prüfe API-Key und Anfrage. Details: $bodyText"))
        }
    }

    private suspend fun requestCompletion(
        messages: List<ChatMessage>,
        model: String,
        temperature: Double,
        maxTokens: Int
    ): Result<String> {
        return try {
            val request = ChatCompletionRequest(
                model = model,
                messages = messages,
                temperature = temperature,
                maxTokens = maxTokens
            )

            val response = client.post("chat/completions") {
                headers {
                    if (apiKey.isNotBlank()) {
                        append("Authorization", "Bearer $apiKey")
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val validationResult = validateJsonResponse(response)
                    if (validationResult.isFailure) {
                        return validationResult.exceptionOrNull()?.let { Result.failure(it) }
                            ?: Result.failure(ApiException("Unbekannter Validierungsfehler"))
                    }

                    val completionResponse = validationResult.getOrThrow()
                    val suggestionsText = completionResponse.choices.firstOrNull()?.message?.content
                        ?: return Result.failure(ApiException("Leere Antwort von der API"))

                    Result.success(suggestionsText)
                }
                HttpStatusCode.Unauthorized -> {
                    Result.failure(ApiException("API-Key prüfen"))
                }
                HttpStatusCode.Forbidden -> {
                    Result.failure(ApiException("Zugriff verweigert"))
                }
                HttpStatusCode.TooManyRequests -> {
                    Result.failure(ApiException("Bitte kurz warten"))
                }
                else -> {
                    Result.failure(ApiException("API-Fehler: ${response.status}"))
                }
            }
        } catch (e: HttpRequestTimeoutException) {
            Result.failure(ApiException("Timeout: Bitte erneut versuchen"))
        } catch (e: UnknownHostException) {
            Result.failure(ApiException("Kein Internet"))
        } catch (e: IOException) {
            Result.failure(ApiException("Netzwerkfehler"))
        } catch (e: Exception) {
            Result.failure(ApiException("Unerwarteter Fehler: ${e.message}"))
        }
    }

    private fun buildRetryPrompt(originalPrompt: String): String {
        return "$originalPrompt\n\nWichtig: Antworte ausschliesslich mit gueltigem JSON, ohne Markdown oder zusaetzliche Zeichen."
    }

    suspend fun generateSuggestions(
        systemPrompt: String,
        userPrompt: String,
        model: String,
        contextMessages: List<ChatMessage> = emptyList()
    ): Result<List<String>> {
        val baseMessages = buildList {
            add(ChatMessage(role = "system", content = systemPrompt))
            addAll(contextMessages)
            add(ChatMessage(role = "user", content = userPrompt))
        }

        val initialResult = requestCompletion(
            messages = baseMessages,
            model = model,
            temperature = 0.7,
            maxTokens = 500
        )

        if (initialResult.isFailure) {
            return Result.failure(initialResult.exceptionOrNull() ?: ApiException("Unbekannter Fehler"))
        }

        val initialText = initialResult.getOrThrow()
        val initialParse = ParseSuggestions.parseSuggestionsResponseDetailed(initialText)

        if (initialParse.source == ParseSource.HEURISTIC) {
            val retryMessages = buildList {
                add(ChatMessage(role = "system", content = systemPrompt))
                addAll(contextMessages)
                add(ChatMessage(role = "user", content = buildRetryPrompt(userPrompt)))
            }

            val retryResult = requestCompletion(
                messages = retryMessages,
                model = model,
                temperature = 0.3,
                maxTokens = 500
            )

            if (retryResult.isSuccess) {
                val retryText = retryResult.getOrThrow()
                val retryParse = ParseSuggestions.parseSuggestionsResponseDetailed(retryText)
                if (retryParse.source != ParseSource.HEURISTIC) {
                    return Result.success(retryParse.suggestions)
                }
            }
        }

        return Result.success(initialParse.suggestions)
    }

    suspend fun rewriteSuggestion(
        systemPrompt: String,
        userPrompt: String,
        model: String
    ): Result<String> {
        val messages = listOf(
            ChatMessage(role = "system", content = systemPrompt),
            ChatMessage(role = "user", content = userPrompt)
        )

        val result = requestCompletion(
            messages = messages,
            model = model,
            temperature = 0.7,
            maxTokens = 200
        )

        return result.map { rewrittenText ->
            ParseSuggestions.parseRewriteResponse(rewrittenText)
        }
    }

    fun close() {
        client.close()
    }
}

class ApiException(message: String) : Exception(message)
