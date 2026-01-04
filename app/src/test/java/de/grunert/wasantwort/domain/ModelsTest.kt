package de.grunert.wasantwort.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ModelsTest {

    @Test
    fun `Tone enum has correct display names`() {
        assertEquals("Freundlich", Tone.FREUNDLICH.displayName)
        assertEquals("Neutral", Tone.NEUTRAL.displayName)
        assertEquals("Herzlich", Tone.HERZLICH.displayName)
    }

    @Test
    fun `Goal enum has correct display names`() {
        assertEquals("Zusagen", Goal.ZUSAGEN.displayName)
        assertEquals("Absagen", Goal.ABSAGEN.displayName)
        assertEquals("Nachfragen", Goal.NACHRAGEN.displayName)
    }

    @Test
    fun `Length enum has correct display names`() {
        assertEquals("1 Satz", Length.EIN_SATZ.displayName)
        assertEquals("Kurz", Length.KURZ.displayName)
        assertEquals("Normal", Length.NORMAL.displayName)
    }

    @Test
    fun `EmojiLevel enum has correct display names`() {
        assertEquals("Aus", EmojiLevel.AUS.displayName)
        assertEquals("Wenig", EmojiLevel.WENIG.displayName)
        assertEquals("Normal", EmojiLevel.NORMAL.displayName)
    }

    @Test
    fun `Formality enum has correct display names`() {
        assertEquals("Du", Formality.DU.displayName)
        assertEquals("Sie", Formality.SIE.displayName)
    }

    @Test
    fun `RewriteType enum has correct display names`() {
        assertEquals("Kürzer", RewriteType.KUERZER.displayName)
        assertEquals("Freundlicher", RewriteType.FREUNDLICHER.displayName)
        assertEquals("Direkter", RewriteType.DIREKTER.displayName)
        assertEquals("Ohne Emojis", RewriteType.OHNE_EMOJIS.displayName)
        assertEquals("Mit Rückfrage", RewriteType.MIT_RUECKFRAGE.displayName)
    }

    @Test
    fun `ModelConfig has correct structure`() {
        val model = ModelConfig(
            id = "test-model",
            displayName = "Test Model",
            isPremium = true,
            defaultBaseUrl = "https://example.com",
            defaultApiKey = "test-key"
        )

        assertEquals("test-model", model.id)
        assertEquals("Test Model", model.displayName)
        assertTrue(model.isPremium)
        assertEquals("https://example.com", model.defaultBaseUrl)
        assertEquals("test-key", model.defaultApiKey)
    }

    @Test
    fun `PredefinedModels has all expected models`() {
        val allModels = PredefinedModels.ALL_MODELS

        assertEquals(4, allModels.size)
        assertTrue(allModels.contains(PredefinedModels.LLAMA_3_3_70B))
        assertTrue(allModels.contains(PredefinedModels.MIMO_V2_FLASH))
        assertTrue(allModels.contains(PredefinedModels.GPT_4O_MINI))
        assertTrue(allModels.contains(PredefinedModels.CLAUDE_HAIKU_4_5))
    }

    @Test
    fun `PredefinedModels free models are not marked as premium`() {
        assertFalse(PredefinedModels.LLAMA_3_3_70B.isPremium)
        assertFalse(PredefinedModels.MIMO_V2_FLASH.isPremium)
    }

    @Test
    fun `PredefinedModels premium models are marked as premium`() {
        assertTrue(PredefinedModels.GPT_4O_MINI.isPremium)
        assertTrue(PredefinedModels.CLAUDE_HAIKU_4_5.isPremium)
    }

    @Test
    fun `findById returns correct model when ID exists`() {
        val model = PredefinedModels.findById("meta-llama/llama-3.3-70b-instruct:free")

        assertNotNull(model)
        assertEquals("Llama 3.3 70B (Free)", model?.displayName)
        assertFalse(model?.isPremium ?: true)
    }

    @Test
    fun `findById returns null when ID does not exist`() {
        val model = PredefinedModels.findById("non-existent-model")

        assertNull(model)
    }

    @Test
    fun `all predefined models have OpenRouter base URL`() {
        PredefinedModels.ALL_MODELS.forEach { model ->
            assertEquals("https://openrouter.ai/api/v1", model.defaultBaseUrl)
        }
    }

    @Test
    fun `all predefined models have default API key configured`() {
        PredefinedModels.ALL_MODELS.forEach { model ->
            assertTrue(model.defaultApiKey.isNotEmpty())
        }
    }
}
