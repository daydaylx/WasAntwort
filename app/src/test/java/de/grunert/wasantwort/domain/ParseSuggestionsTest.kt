package de.grunert.wasantwort.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParseSuggestionsTest {

    @Test
    fun `parseSuggestionsResponse parses valid JSON with suggestions array`() {
        val json = """{"suggestions": ["Antwort 1", "Antwort 2", "Antwort 3", "Antwort 4", "Antwort 5"]}"""
        
        val result = ParseSuggestions.parseSuggestionsResponse(json)
        
        assertEquals(5, result.size)
        assertEquals("Antwort 1", result[0])
        assertEquals("Antwort 5", result[4])
    }

    @Test
    fun `parseSuggestionsResponse parses OpenAI format with choices`() {
        val json = """
        {
            "choices": [
                {"message": {"content": "{\"suggestions\": [\"A1\", \"A2\", \"A3\", \"A4\", \"A5\"]}"}},
                {"message": {"content": "extra"}}
            ]
        }
        """
        
        val result = ParseSuggestions.parseSuggestionsResponse(json)
        
        assertEquals(5, result.size)
    }

    @Test
    fun `parseSuggestionsResponse falls back to heuristic parsing for malformed JSON`() {
        val malformedJson = "This is not JSON but has\nmultiple lines\nof text\nwith answers\nand more"
        
        val result = ParseSuggestions.parseSuggestionsResponse(malformedJson)
        
        assertEquals(5, result.size)
        assertTrue(result.all { it.isNotBlank() })
    }

    @Test
    fun `parseSuggestionsResponse always returns exactly 5 suggestions`() {
        val emptyJson = "{}"
        
        val result = ParseSuggestions.parseSuggestionsResponse(emptyJson)
        
        assertEquals(5, result.size)
    }

    @Test
    fun `parseRewriteResponse extracts text from JSON`() {
        val json = """{"text": "Überarbeitete Antwort"}"""
        
        val result = ParseSuggestions.parseRewriteResponse(json)
        
        assertEquals("Überarbeitete Antwort", result)
    }

    @Test
    fun `parseRewriteResponse handles malformed JSON gracefully`() {
        val malformed = "Some random text without JSON"
        
        val result = ParseSuggestions.parseRewriteResponse(malformed)
        
        assertTrue(result.isNotBlank())
    }
}



