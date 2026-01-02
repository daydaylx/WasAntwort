package de.grunert.wasantwort.data

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AiClientTest {

    @Test
    fun `ApiException is thrown with correct message`() {
        val exception = ApiException("Test error")
        assertEquals("Test error", exception.message)
    }

    @Test
    fun `ChatCompletionRequest serializes correctly`() {
        val request = ChatCompletionRequest(
            model = "test-model",
            messages = listOf(
                ChatMessage(role = "system", content = "System prompt"),
                ChatMessage(role = "user", content = "User message")
            ),
            temperature = 0.7,
            maxTokens = 500
        )

        assertEquals("test-model", request.model)
        assertEquals(2, request.messages.size)
        assertEquals(0.7, request.temperature)
        assertEquals(500, request.maxTokens)
    }

    @Test
    fun `ChatCompletionResponse deserializes correctly`() {
        val response = ChatCompletionResponse(
            choices = listOf(
                Choice(
                    message = ChatMessage(
                        role = "assistant",
                        content = "Response text"
                    )
                )
            )
        )

        assertEquals(1, response.choices.size)
        assertEquals("Response text", response.choices[0].message.content)
    }

    @Test
    fun `ChatMessage has correct structure`() {
        val message = ChatMessage(role = "user", content = "Test content")

        assertEquals("user", message.role)
        assertEquals("Test content", message.content)
    }
}
