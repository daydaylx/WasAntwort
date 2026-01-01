package de.grunert.replyhelper.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PromptBuilderTest {

    @Test
    fun `buildGeneratePrompt includes all parameters`() {
        val originalMessage = "Kannst du morgen kommen?"
        val tone = Tone.FREUNDLICH
        val goal = Goal.ZUSAGEN
        val length = Length.KURZ
        val emojiLevel = EmojiLevel.WENIG
        val formality = Formality.DU

        val prompt = PromptBuilder.buildGeneratePrompt(
            originalMessage,
            tone,
            goal,
            length,
            emojiLevel,
            formality
        )

        assertTrue(prompt.contains(originalMessage))
        assertTrue(prompt.contains("freundlich"))
        assertTrue(prompt.contains("Zusage"))
        assertTrue(prompt.contains("kurz"))
        assertTrue(prompt.contains("sparsam"))
        assertTrue(prompt.contains("Du"))
    }

    @Test
    fun `buildRewritePrompt includes selected suggestion and rewrite type`() {
        val originalMessage = "Test message"
        val selectedSuggestion = "Ok, passt!"
        val rewriteType = RewriteType.KUERZER

        val prompt = PromptBuilder.buildRewritePrompt(
            originalMessage,
            selectedSuggestion,
            rewriteType
        )

        assertTrue(prompt.contains(selectedSuggestion))
        assertTrue(prompt.contains("Kürze"))
    }

    @Test
    fun `system prompt enforces German and 5 suggestions`() {
        val systemPrompt = PromptBuilder.getSystemPrompt()
        
        assertTrue(systemPrompt.contains("Deutsch"))
        assertTrue(systemPrompt.contains("5"))
        assertTrue(systemPrompt.contains("JSON"))
    }
}


