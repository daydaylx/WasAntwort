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

data class ModelConfig(
    val id: String,
    val displayName: String,
    val isPremium: Boolean,
    val defaultBaseUrl: String = "https://openrouter.ai/api/v1",
    val defaultApiKey: String? = null
)

object PredefinedModels {
    private const val DEFAULT_API_KEY = "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"

    val LLAMA_3_3_70B = ModelConfig(
        id = "meta-llama/llama-3.3-70b-instruct:free",
        displayName = "Llama 3.3 70B (Free)",
        isPremium = false,
        defaultApiKey = DEFAULT_API_KEY
    )

    val MIMO_V2_FLASH = ModelConfig(
        id = "xiaomi/mimo-v2-flash:free",
        displayName = "Mimo V2 Flash (Free)",
        isPremium = false,
        defaultApiKey = DEFAULT_API_KEY
    )

    val GPT_4O_MINI = ModelConfig(
        id = "openai/gpt-4o-mini",
        displayName = "GPT-4o Mini (Premium)",
        isPremium = true,
        defaultBaseUrl = "https://openrouter.ai/api/v1",
        defaultApiKey = DEFAULT_API_KEY
    )

    val CLAUDE_HAIKU_4_5 = ModelConfig(
        id = "anthropic/claude-haiku-4.5",
        displayName = "Claude Haiku 4.5 (Premium)",
        isPremium = true,
        defaultBaseUrl = "https://openrouter.ai/api/v1",
        defaultApiKey = DEFAULT_API_KEY
    )

    val ALL_MODELS = listOf(
        LLAMA_3_3_70B,
        MIMO_V2_FLASH,
        GPT_4O_MINI,
        CLAUDE_HAIKU_4_5
    )

    fun findById(id: String): ModelConfig? = ALL_MODELS.find { it.id == id }
}



