package de.grunert.wasantwort.domain

enum class Tone(val displayName: String) {
    FREUNDLICH("Freundlich"),
    NEUTRAL("Neutral"),
    KURZ("Kurz"),
    HERZLICH("Herzlich"),
    BESTIMMT("Bestimmt"),
    FLIRTY("Flirty")
}

enum class Goal(val displayName: String) {
    ZUSAGEN("Zusagen"),
    ABSAGEN("Absagen"),
    VERSCHIEBEN("Verschieben"),
    NACHRAGEN("Nachfragen"),
    BEDANKEN("Bedanken"),
    ABGRENZEN("Abgrenzen")
}

enum class Length(val displayName: String) {
    EIN_SATZ("1 Satz"),
    KURZ("Kurz"),
    NORMAL("Normal")
}

enum class EmojiLevel(val displayName: String) {
    AUS("Aus"),
    WENIG("Wenig"),
    NORMAL("Normal")
}

enum class Formality(val displayName: String) {
    DU("Du"),
    SIE("Sie")
}

enum class RewriteType(val displayName: String) {
    KUERZER("Kürzer"),
    FREUNDLICHER("Freundlicher"),
    DIREKTER("Direkter"),
    OHNE_EMOJIS("Ohne Emojis"),
    MIT_RUECKFRAGE("Mit Rückfrage")
}



