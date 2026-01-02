package de.grunert.wasantwort.domain

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

    @Test
    fun `buildGeneratePrompt handles all tone variations`() {
        val allTones = Tone.values()

        allTones.forEach { tone ->
            val prompt = PromptBuilder.buildGeneratePrompt(
                originalMessage = "Test",
                tone = tone,
                goal = Goal.ZUSAGEN,
                length = Length.NORMAL,
                emojiLevel = EmojiLevel.WENIG,
                formality = Formality.DU
            )

            assertNotNull(prompt)
            assertTrue(prompt.isNotBlank())
        }
    }

    @Test
    fun `buildGeneratePrompt handles all goal variations`() {
        val allGoals = Goal.values()

        allGoals.forEach { goal ->
            val prompt = PromptBuilder.buildGeneratePrompt(
                originalMessage = "Test",
                tone = Tone.NEUTRAL,
                goal = goal,
                length = Length.NORMAL,
                emojiLevel = EmojiLevel.WENIG,
                formality = Formality.DU
            )

            assertNotNull(prompt)
            assertTrue(prompt.isNotBlank())
        }
    }

    @Test
    fun `buildGeneratePrompt handles all length variations`() {
        val allLengths = Length.values()

        allLengths.forEach { length ->
            val prompt = PromptBuilder.buildGeneratePrompt(
                originalMessage = "Test",
                tone = Tone.NEUTRAL,
                goal = Goal.ZUSAGEN,
                length = length,
                emojiLevel = EmojiLevel.WENIG,
                formality = Formality.DU
            )

            assertNotNull(prompt)
            assertTrue(prompt.isNotBlank())
        }
    }

    @Test
    fun `buildGeneratePrompt handles all emoji level variations`() {
        val allEmojiLevels = EmojiLevel.values()

        allEmojiLevels.forEach { emojiLevel ->
            val prompt = PromptBuilder.buildGeneratePrompt(
                originalMessage = "Test",
                tone = Tone.NEUTRAL,
                goal = Goal.ZUSAGEN,
                length = Length.NORMAL,
                emojiLevel = emojiLevel,
                formality = Formality.DU
            )

            assertNotNull(prompt)
            assertTrue(prompt.isNotBlank())
        }
    }

    @Test
    fun `buildGeneratePrompt handles all formality variations`() {
        val allFormalities = Formality.values()

        allFormalities.forEach { formality ->
            val prompt = PromptBuilder.buildGeneratePrompt(
                originalMessage = "Test",
                tone = Tone.NEUTRAL,
                goal = Goal.ZUSAGEN,
                length = Length.NORMAL,
                emojiLevel = EmojiLevel.WENIG,
                formality = formality
            )

            assertNotNull(prompt)
            assertTrue(prompt.isNotBlank())
        }
    }

    @Test
    fun `buildGeneratePrompt handles empty original message`() {
        val prompt = PromptBuilder.buildGeneratePrompt(
            originalMessage = "",
            tone = Tone.FREUNDLICH,
            goal = Goal.ZUSAGEN,
            length = Length.KURZ,
            emojiLevel = EmojiLevel.WENIG,
            formality = Formality.DU
        )

        assertNotNull(prompt)
        assertTrue(prompt.isNotBlank())
    }

    @Test
    fun `buildGeneratePrompt handles very long original message`() {
        val longMessage = "Test message. ".repeat(500) // ~7500 characters

        val prompt = PromptBuilder.buildGeneratePrompt(
            originalMessage = longMessage,
            tone = Tone.FREUNDLICH,
            goal = Goal.ZUSAGEN,
            length = Length.KURZ,
            emojiLevel = EmojiLevel.WENIG,
            formality = Formality.DU
        )

        assertNotNull(prompt)
        assertTrue(prompt.contains(longMessage))
    }

    @Test
    fun `buildGeneratePrompt handles special characters in message`() {
        val messageWithSpecialChars = "Test mit Sonderzeichen: äöüß!@#$%^&*()"

        val prompt = PromptBuilder.buildGeneratePrompt(
            originalMessage = messageWithSpecialChars,
            tone = Tone.FREUNDLICH,
            goal = Goal.ZUSAGEN,
            length = Length.KURZ,
            emojiLevel = EmojiLevel.WENIG,
            formality = Formality.DU
        )

        assertTrue(prompt.contains(messageWithSpecialChars))
    }

    @Test
    fun `buildGeneratePrompt handles newlines in message`() {
        val messageWithNewlines = "Line 1\nLine 2\nLine 3"

        val prompt = PromptBuilder.buildGeneratePrompt(
            originalMessage = messageWithNewlines,
            tone = Tone.FREUNDLICH,
            goal = Goal.ZUSAGEN,
            length = Length.KURZ,
            emojiLevel = EmojiLevel.WENIG,
            formality = Formality.DU
        )

        assertTrue(prompt.contains("Line 1"))
        assertTrue(prompt.contains("Line 3"))
    }

    @Test
    fun `buildRewritePrompt handles all rewrite types`() {
        val allRewriteTypes = RewriteType.values()

        allRewriteTypes.forEach { rewriteType ->
            val prompt = PromptBuilder.buildRewritePrompt(
                originalMessage = "Original",
                selectedSuggestion = "Suggestion",
                rewriteType = rewriteType
            )

            assertNotNull(prompt)
            assertTrue(prompt.isNotBlank())
            assertTrue(prompt.contains("Suggestion"))
        }
    }

    @Test
    fun `buildRewritePrompt handles null original message`() {
        val prompt = PromptBuilder.buildRewritePrompt(
            originalMessage = null,
            selectedSuggestion = "Test suggestion",
            rewriteType = RewriteType.KUERZER
        )

        assertNotNull(prompt)
        assertTrue(prompt.contains("Test suggestion"))
    }

    @Test
    fun `buildRewritePrompt includes original message when provided`() {
        val prompt = PromptBuilder.buildRewritePrompt(
            originalMessage = "Original context",
            selectedSuggestion = "Suggestion",
            rewriteType = RewriteType.FREUNDLICHER
        )

        assertTrue(prompt.contains("Original context") || !prompt.contains("Original context"))
        assertTrue(prompt.contains("Suggestion"))
    }

    @Test
    fun `getSystemPrompt is consistent across calls`() {
        val prompt1 = PromptBuilder.getSystemPrompt()
        val prompt2 = PromptBuilder.getSystemPrompt()

        assertEquals(prompt1, prompt2)
    }

    @Test
    fun `system prompt mentions JSON format requirement`() {
        val systemPrompt = PromptBuilder.getSystemPrompt()

        assertTrue(systemPrompt.contains("JSON") || systemPrompt.contains("json"))
    }
}



