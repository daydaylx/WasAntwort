package de.grunert.wasantwort.domain

/**
 * Preset-Kombinationen für häufige Stil-Konfigurationen
 */
enum class StylePreset(
    val displayName: String,
    val tone: Tone,
    val goal: Goal,
    val length: Length,
    val emojiLevel: EmojiLevel,
    val formality: Formality
) {
    FREUNDLICH_STANDARD(
        displayName = "Freundlich (Standard)",
        tone = Tone.FREUNDLICH,
        goal = Goal.NACHRAGEN,
        length = Length.NORMAL,
        emojiLevel = EmojiLevel.WENIG,
        formality = Formality.DU
    ),
    KURZ_KLAR(
        displayName = "Kurz & klar",
        tone = Tone.KURZ,
        goal = Goal.NACHRAGEN,
        length = Length.KURZ,
        emojiLevel = EmojiLevel.AUS,
        formality = Formality.DU
    ),
    HOEFLICH_ABLEHNEN(
        displayName = "Höflich ablehnen",
        tone = Tone.FREUNDLICH,
        goal = Goal.ABSAGEN,
        length = Length.NORMAL,
        emojiLevel = EmojiLevel.AUS,
        formality = Formality.SIE
    )
}


