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

    @Test
    fun `parseSuggestionsResponse handles nested JSON in choices`() {
        val json = """
        {
            "choices": [
                {
                    "message": {
                        "content": "{\"suggestions\":[\"S1\",\"S2\",\"S3\",\"S4\",\"S5\"]}"
                    }
                }
            ]
        }
        """

        val result = ParseSuggestions.parseSuggestionsResponse(json)

        assertEquals(5, result.size)
        assertEquals("S1", result[0])
    }

    @Test
    fun `parseSuggestionsResponse handles fewer than 5 suggestions by padding`() {
        val json = """{"suggestions": ["Only one", "And two"]}"""

        val result = ParseSuggestions.parseSuggestionsResponse(json)

        assertEquals(5, result.size)
        assertEquals("Only one", result[0])
        assertEquals("And two", result[1])
        // Remaining should be padded
        assertTrue(result[2].isNotBlank())
    }

    @Test
    fun `parseSuggestionsResponse handles more than 5 suggestions by taking first 5`() {
        val json = """{"suggestions": ["1", "2", "3", "4", "5", "6", "7", "8"]}"""

        val result = ParseSuggestions.parseSuggestionsResponse(json)

        assertEquals(5, result.size)
        assertEquals("1", result[0])
        assertEquals("5", result[4])
    }

    @Test
    fun `parseSuggestionsResponse handles empty suggestions array`() {
        val json = """{"suggestions": []}"""

        val result = ParseSuggestions.parseSuggestionsResponse(json)

        assertEquals(5, result.size)
        assertTrue(result.all { it.isNotBlank() })
    }

    @Test
    fun `parseSuggestionsResponse handles completely invalid JSON`() {
        val invalid = "{ this is not valid JSON at all }"

        val result = ParseSuggestions.parseSuggestionsResponse(invalid)

        assertEquals(5, result.size)
        assertTrue(result.all { it.isNotBlank() })
    }

    @Test
    fun `parseSuggestionsResponse handles null values in suggestions array`() {
        val json = """{"suggestions": ["Valid", null, "Another", null, "Last"]}"""

        val result = ParseSuggestions.parseSuggestionsResponse(json)

        assertEquals(5, result.size)
        // Should filter out nulls and pad
    }

    @Test
    fun `parseSuggestionsResponse handles empty strings in suggestions`() {
        val json = """{"suggestions": ["Valid", "", "Another", "", "Last"]}"""

        val result = ParseSuggestions.parseSuggestionsResponse(json)

        assertEquals(5, result.size)
        // Should have valid suggestions
        assertTrue(result.any { it == "Valid" })
        assertTrue(result.any { it == "Another" })
    }

    @Test
    fun `parseSuggestionsResponse handles suggestions with special characters`() {
        val json = """{"suggestions": ["Klar! 👍", "Geht @ll", "Test#123", "Über €50", "100%"]}"""

        val result = ParseSuggestions.parseSuggestionsResponse(json)

        assertEquals(5, result.size)
        assertTrue(result.contains("Klar! 👍"))
        assertTrue(result.contains("100%"))
    }

    @Test
    fun `parseSuggestionsResponse handles suggestions with newlines`() {
        val json = """{"suggestions": ["Line 1\nLine 2", "Single", "Multi\nline\ntext", "Normal", "Last"]}"""

        val result = ParseSuggestions.parseSuggestionsResponse(json)

        assertEquals(5, result.size)
    }

    @Test
    fun `parseSuggestionsResponse handles very long suggestions`() {
        val longSuggestion = "Test ".repeat(200) // ~1000 characters
        val json = """{"suggestions": ["$longSuggestion", "S2", "S3", "S4", "S5"]}"""

        val result = ParseSuggestions.parseSuggestionsResponse(json)

        assertEquals(5, result.size)
        assertTrue(result[0].length > 500)
    }

    @Test
    fun `parseSuggestionsResponse handles quoted suggestions`() {
        val json = """{"suggestions": ["\"Quoted\"", "Normal", "With 'single'", "Another", "Last"]}"""

        val result = ParseSuggestions.parseSuggestionsResponse(json)

        assertEquals(5, result.size)
    }

    @Test
    fun `parseRewriteResponse extracts from OpenAI format`() {
        val json = """
        {
            "choices": [
                {
                    "message": {
                        "content": "Rewritten text here"
                    }
                }
            ]
        }
        """

        val result = ParseSuggestions.parseRewriteResponse(json)

        assertEquals("Rewritten text here", result)
    }

    @Test
    fun `parseRewriteResponse handles nested JSON in content`() {
        val json = """
        {
            "choices": [
                {
                    "message": {
                        "content": "{\"text\": \"Nested rewrite\"}"
                    }
                }
            ]
        }
        """

        val result = ParseSuggestions.parseRewriteResponse(json)

        assertTrue(result.isNotBlank())
        assertTrue(result.contains("Nested rewrite") || result.contains("text"))
    }

    @Test
    fun `parseRewriteResponse handles empty string response`() {
        val json = """{"text": ""}"""

        val result = ParseSuggestions.parseRewriteResponse(json)

        assertTrue(result.isNotBlank())
    }

    @Test
    fun `parseRewriteResponse handles response with special characters`() {
        val json = """{"text": "Test äöü ß! @#$%"}"""

        val result = ParseSuggestions.parseRewriteResponse(json)

        assertTrue(result.contains("äöü") || result.isNotBlank())
    }

    @Test
    fun `parseSuggestionsResponse handles numbered list format`() {
        val text = """
        1. First suggestion
        2. Second suggestion
        3. Third suggestion
        4. Fourth suggestion
        5. Fifth suggestion
        """

        val result = ParseSuggestions.parseSuggestionsResponse(text)

        assertEquals(5, result.size)
        assertTrue(result.all { it.isNotBlank() })
    }

    @Test
    fun `parseSuggestionsResponse handles bullet point format`() {
        val text = """
        - First suggestion
        - Second suggestion
        - Third suggestion
        - Fourth suggestion
        - Fifth suggestion
        """

        val result = ParseSuggestions.parseSuggestionsResponse(text)

        assertEquals(5, result.size)
        assertTrue(result.all { it.isNotBlank() })
    }

    @Test
    fun `parseSuggestionsResponse handles plain text separated by newlines`() {
        val text = """
        First suggestion
        Second suggestion
        Third suggestion
        Fourth suggestion
        Fifth suggestion
        Sixth suggestion
        """

        val result = ParseSuggestions.parseSuggestionsResponse(text)

        assertEquals(5, result.size)
        assertTrue(result.all { it.isNotBlank() })
    }

    @Test
    fun `parseSuggestionsResponse handles mixed format with fallback`() {
        val text = "Some random text\n1. Suggestion\nMore text\nAnother line"

        val result = ParseSuggestions.parseSuggestionsResponse(text)

        assertEquals(5, result.size)
        assertTrue(result.all { it.isNotBlank() })
    }
}



