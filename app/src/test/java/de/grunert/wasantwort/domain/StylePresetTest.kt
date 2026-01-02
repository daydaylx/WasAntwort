package de.grunert.wasantwort.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StylePresetTest {

    @Test
    fun `FREUNDLICH_STANDARD has correct configuration`() {
        val preset = StylePreset.FREUNDLICH_STANDARD

        assertEquals("Freundlich (Standard)", preset.displayName)
        assertEquals(Tone.FREUNDLICH, preset.tone)
        assertEquals(Goal.NACHRAGEN, preset.goal)
        assertEquals(Length.NORMAL, preset.length)
        assertEquals(EmojiLevel.WENIG, preset.emojiLevel)
        assertEquals(Formality.DU, preset.formality)
    }

    @Test
    fun `KURZ_KLAR has correct configuration`() {
        val preset = StylePreset.KURZ_KLAR

        assertEquals("Kurz & klar", preset.displayName)
        assertEquals(Tone.KURZ, preset.tone)
        assertEquals(Goal.NACHRAGEN, preset.goal)
        assertEquals(Length.KURZ, preset.length)
        assertEquals(EmojiLevel.AUS, preset.emojiLevel)
        assertEquals(Formality.DU, preset.formality)
    }

    @Test
    fun `HOEFLICH_ABLEHNEN has correct configuration`() {
        val preset = StylePreset.HOEFLICH_ABLEHNEN

        assertEquals("Höflich ablehnen", preset.displayName)
        assertEquals(Tone.FREUNDLICH, preset.tone)
        assertEquals(Goal.ABSAGEN, preset.goal)
        assertEquals(Length.NORMAL, preset.length)
        assertEquals(EmojiLevel.AUS, preset.emojiLevel)
        assertEquals(Formality.SIE, preset.formality)
    }

    @Test
    fun `all presets have unique display names`() {
        val allPresets = StylePreset.values()
        val displayNames = allPresets.map { it.displayName }

        assertEquals(displayNames.size, displayNames.distinct().size)
    }

    @Test
    fun `all presets have non-empty display names`() {
        StylePreset.values().forEach { preset ->
            assertTrue(preset.displayName.isNotBlank(), "Preset ${preset.name} has blank display name")
        }
    }

    @Test
    fun `KURZ_KLAR preset has no emojis`() {
        // This preset should be for short, clear communication without emojis
        val preset = StylePreset.KURZ_KLAR

        assertEquals(EmojiLevel.AUS, preset.emojiLevel)
        assertEquals(Tone.KURZ, preset.tone)
        assertEquals(Length.KURZ, preset.length)
    }

    @Test
    fun `HOEFLICH_ABLEHNEN uses formal address`() {
        // This preset should use formal "Sie" for polite declining
        val preset = StylePreset.HOEFLICH_ABLEHNEN

        assertEquals(Formality.SIE, preset.formality)
        assertEquals(Goal.ABSAGEN, preset.goal)
    }

    @Test
    fun `FREUNDLICH_STANDARD uses informal address`() {
        // Standard friendly preset should use informal "Du"
        val preset = StylePreset.FREUNDLICH_STANDARD

        assertEquals(Formality.DU, preset.formality)
        assertEquals(Tone.FREUNDLICH, preset.tone)
    }

    @Test
    fun `presets cover different use cases`() {
        val presets = StylePreset.values()

        // Should have at least one preset for different goals
        val goals = presets.map { it.goal }.toSet()
        assertTrue(goals.contains(Goal.ABSAGEN), "Should have preset for declining")
        assertTrue(goals.contains(Goal.NACHRAGEN), "Should have preset for asking")

        // Should have presets with different formality levels
        val formalities = presets.map { it.formality }.toSet()
        assertTrue(formalities.size > 1, "Should have presets with different formality levels")
    }

    @Test
    fun `all preset names match enum naming convention`() {
        StylePreset.values().forEach { preset ->
            // Enum names should be UPPER_CASE
            assertTrue(preset.name.matches(Regex("^[A-Z_]+$")),
                "Preset name ${preset.name} doesn't match UPPER_CASE convention")
        }
    }

    @Test
    fun `exactly 3 presets are defined`() {
        assertEquals(3, StylePreset.values().size,
            "Expected exactly 3 presets (as per MVP design)")
    }
}
