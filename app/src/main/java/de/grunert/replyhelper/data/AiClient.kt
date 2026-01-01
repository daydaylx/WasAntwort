package de.grunert.replyhelper.data

import de.grunert.replyhelper.domain.ParseSuggestions
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultrequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.timeout
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.UnknownHostException

class AiClient(private val baseUrl: String, private val apiKey: String) {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    // Logging disabled to avoid logging sensitive data
                    // Can be enabled for debugging if needed
                }
            }
            level = LogLevel.NONE
        }
        defaultRequest {
            this.baseUrl = baseUrl
            timeout {
                connectTimeoutMillis = 10_000
                requestTimeoutMillis = 30_000
            }
        }
    }

    suspend fun generateSuggestions(
        systemPrompt: String,
        userPrompt: String,
        model: String
    ): Result<List<String>> {
        return try {
            val request = ChatCompletionRequest(
                model = model,
                messages = listOf(
                    ChatMessage(role = "system", content = systemPrompt),
                    ChatMessage(role = "user", content = userPrompt)
                ),
                temperature = 0.7,
                maxTokens = 500
            )

            val response = client.post("/chat/completions") {
                headers {
                    append("Authorization", "Bearer $apiKey")
                }
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val completionResponse: ChatCompletionResponse = response.body()
                    val suggestionsText = completionResponse.choices.firstOrNull()?.message?.content
                        ?: return Result.failure(ApiException("Leere Antwort von der API"))

                    val suggestions = ParseSuggestions.parseSuggestionsResponse(suggestionsText)
                    Result.success(suggestions)
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

    suspend fun rewriteSuggestion(
        systemPrompt: String,
        userPrompt: String,
        model: String
    ): Result<String> {
        return try {
            val request = ChatCompletionRequest(
                model = model,
                messages = listOf(
                    ChatMessage(role = "system", content = systemPrompt),
                    ChatMessage(role = "user", content = userPrompt)
                ),
                temperature = 0.7,
                maxTokens = 200
            )

            val response = client.post("/chat/completions") {
                headers {
                    append("Authorization", "Bearer $apiKey")
                }
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val completionResponse: ChatCompletionResponse = response.body()
                    val rewrittenText = completionResponse.choices.firstOrNull()?.message?.content
                        ?: return Result.failure(ApiException("Leere Antwort von der API"))

                    val parsed = ParseSuggestions.parseRewriteResponse(rewrittenText)
                    Result.success(parsed)
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

    fun close() {
        client.close()
    }
}

class ApiException(message: String) : Exception(message)

