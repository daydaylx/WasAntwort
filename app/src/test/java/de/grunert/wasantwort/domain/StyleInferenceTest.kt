package de.grunert.wasantwort.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StyleInferenceTest {

    @Test
    fun `infers formal tone and formality from formal greeting`() {
        val input = "Sehr geehrte Damen und Herren, vielen Dank. Mit freundlichen Gruessen"

        val result = StyleInference.infer(input)

        assertEquals(Formality.SIE, result.formality)
        assertEquals(Tone.NEUTRAL, result.tone)
    }

    @Test
    fun `infers informal tone and formality from casual greeting`() {
        val input = "Hey, kannst du kurz helfen?"

        val result = StyleInference.infer(input)

        assertEquals(Formality.DU, result.formality)
        assertEquals(Tone.FREUNDLICH, result.tone)
    }

    @Test
    fun `infers flirty tone when explicit cues are present`() {
        val input = "Hi Schatz, wie war dein Tag?"

        val result = StyleInference.infer(input)

        assertEquals(Formality.DU, result.formality)
        assertEquals(Tone.FLIRTY, result.tone)
    }

    @Test
    fun `returns nulls when no signals are present`() {
        val input = "Test message without signals."

        val result = StyleInference.infer(input)

        assertNull(result.formality)
        assertNull(result.tone)
    }
}
