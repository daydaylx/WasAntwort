package de.grunert.wasantwort.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ConversationHistoryTest {

    @Test
    fun `ConversationEntry creates with all fields`() {
        val entry = ConversationEntry(
            id = "test-id",
            timestamp = 123456789L,
            inputText = "Test input",
            tone = Tone.FREUNDLICH,
            goal = Goal.ZUSAGEN,
            length = Length.KURZ,
            emojiLevel = EmojiLevel.WENIG,
            formality = Formality.DU,
            suggestions = listOf("Suggestion 1", "Suggestion 2")
        )

        assertEquals("test-id", entry.id)
        assertEquals(123456789L, entry.timestamp)
        assertEquals("Test input", entry.inputText)
        assertEquals(Tone.FREUNDLICH, entry.tone)
        assertEquals(Goal.ZUSAGEN, entry.goal)
        assertEquals(Length.KURZ, entry.length)
        assertEquals(EmojiLevel.WENIG, entry.emojiLevel)
        assertEquals(Formality.DU, entry.formality)
        assertEquals(2, entry.suggestions.size)
    }

    @Test
    fun `ConversationContext creates with messages`() {
        val messages = listOf(
            ContextMessage(role = "user", content = "Hello", timestamp = 1000L),
            ContextMessage(role = "assistant", content = "Hi there", timestamp = 2000L)
        )
        val context = ConversationContext(messages = messages)

        assertEquals(2, context.messages.size)
        assertEquals("user", context.messages[0].role)
        assertEquals("Hello", context.messages[0].content)
    }

    @Test
    fun `ContextMessage creates with correct structure`() {
        val message = ContextMessage(
            role = "assistant",
            content = "Test content",
            timestamp = 999L
        )

        assertEquals("assistant", message.role)
        assertEquals("Test content", message.content)
        assertEquals(999L, message.timestamp)
    }

    @Test
    fun `ConversationEntry handles empty suggestions list`() {
        val entry = ConversationEntry(
            id = "test",
            timestamp = 0L,
            inputText = "Input",
            tone = Tone.NEUTRAL,
            goal = Goal.NACHRAGEN,
            length = Length.NORMAL,
            emojiLevel = EmojiLevel.AUS,
            formality = Formality.SIE,
            suggestions = emptyList()
        )

        assertTrue(entry.suggestions.isEmpty())
    }

    @Test
    fun `ConversationContext handles empty messages list`() {
        val context = ConversationContext(messages = emptyList())

        assertTrue(context.messages.isEmpty())
    }

    @Test
    fun `ContextMessage supports both user and assistant roles`() {
        val userMessage = ContextMessage("user", "User says", 1L)
        val assistantMessage = ContextMessage("assistant", "AI responds", 2L)

        assertEquals("user", userMessage.role)
        assertEquals("assistant", assistantMessage.role)
    }
}
