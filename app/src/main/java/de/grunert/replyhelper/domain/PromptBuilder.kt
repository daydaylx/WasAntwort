package de.grunert.replyhelper.domain

object PromptBuilder {
    
    private const val SYSTEM_PROMPT = """Du bist ein Assistent, der kurze, präzise Antwortvorschläge für Nachrichten erstellt.
Regeln:
- Sprache: IMMER Deutsch
- Output: IMMER genau 5 Antwortvorschläge als JSON: {"suggestions": ["Antwort 1", "Antwort 2", "Antwort 3", "Antwort 4", "Antwort 5"]}
- Keine Erklärungen, keine zusätzlichen Texte, nur das JSON
- Keine erfundenen Details oder Kontext
- Wenn die Nachricht unklar ist: mindestens eine der 5 Antworten sollte eine Rückfrage sein
- Halte dich strikt an die vorgegebenen Parameter (Ton, Ziel, Länge, Emojis, Du/Sie)"""

    fun buildGeneratePrompt(
        originalMessage: String,
        tone: Tone,
        goal: Goal,
        length: Length,
        emojiLevel: EmojiLevel,
        formality: Formality
    ): String {
        val toneDesc = when (tone) {
            Tone.FREUNDLICH -> "freundlich und warm"
            Tone.NEUTRAL -> "neutral und sachlich"
            Tone.KURZ -> "sehr kurz und knapp"
            Tone.HERZLICH -> "herzlich und persönlich"
            Tone.BESTIMMT -> "bestimmt und klar"
            Tone.FLIRTY -> "spielerisch und flirtend"
        }

        val goalDesc = when (goal) {
            Goal.ZUSAGEN -> "einer Zusage"
            Goal.ABSAGEN -> "einer höflichen Absage"
            Goal.VERSCHIEBEN -> "einer Verschiebung auf später"
            Goal.NACHRAGEN -> "einer Nachfrage"
            Goal.BEDANKEN -> "einer Dankesbekundung"
            Goal.ABGRENZEN -> "einer höflichen, aber klaren Abgrenzung"
        }

        val lengthDesc = when (length) {
            Length.EIN_SATZ -> "nur einen Satz lang"
            Length.KURZ -> "kurz (2-3 Sätze)"
            Length.NORMAL -> "normal lang (3-5 Sätze)"
        }

        val emojiDesc = when (emojiLevel) {
            EmojiLevel.AUS -> "keine Emojis"
            EmojiLevel.WENIG -> "sparsam mit Emojis (max. 1 pro Antwort)"
            EmojiLevel.NORMAL -> "normale Emoji-Nutzung (2-3 pro Antwort)"
        }

        val formalityDesc = when (formality) {
            Formality.DU -> "Du"
            Formality.SIE -> "Sie"
        }

        return """Originalnachricht:
"$originalMessage"

Erstelle genau 5 Antwortvorschläge mit folgenden Parametern:
- Ton: $toneDesc
- Ziel: $goalDesc
- Länge: $lengthDesc
- Emojis: $emojiDesc
- Anrede: $formalityDesc

Gib nur das JSON zurück, keine weiteren Erklärungen."""
    }

    fun buildRewritePrompt(
        originalMessage: String?,
        selectedSuggestion: String,
        rewriteType: RewriteType
    ): String {
        val instruction = when (rewriteType) {
            RewriteType.KUERZER -> "Kürze diese Antwort deutlich, behalte aber die Kernaussage."
            RewriteType.FREUNDLICHER -> "Mache diese Antwort freundlicher und wärmer."
            RewriteType.DIREKTER -> "Mache diese Antwort direkter und klarer."
            RewriteType.OHNE_EMOJIS -> "Entferne alle Emojis aus dieser Antwort."
            RewriteType.MIT_RUECKFRAGE -> "Füge eine kurze Rückfrage an diese Antwort an."
        }

        val context = if (originalMessage != null) {
            "\nOriginalnachricht: \"$originalMessage\"\n"
        } else {
            "\n"
        }

        return """$context
Aktuelle Antwort:
"$selectedSuggestion"

$instruction

Gib nur das überarbeitete JSON zurück: {"text": "überarbeitete Antwort"}
Keine Erklärungen."""
    }

    fun getSystemPrompt(): String = SYSTEM_PROMPT
}


