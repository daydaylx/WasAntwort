package de.grunert.wasantwort.domain

data class InferredStyle(
    val tone: Tone?,
    val formality: Formality?
)

object StyleInference {

    private val formalSignals = listOf(
        Regex("\\bSehr geehrt(e|er|en|es)?\\b", RegexOption.IGNORE_CASE),
        Regex("\\bSehr geehrte[nr]?\\b", RegexOption.IGNORE_CASE),
        Regex("\\bMit freundlichen Gr(?:ue|\\u00fc)(?:\\u00df|ss)en\\b", RegexOption.IGNORE_CASE),
        Regex("\\bGuten Tag\\b", RegexOption.IGNORE_CASE),
        Regex("\\bHerr\\b"),
        Regex("\\bFrau\\b"),
        Regex("\\bSie\\b"),
        Regex("\\bIhnen\\b"),
        Regex("\\bIhr(e|en|er|em)?\\b")
    )

    private val informalSignals = listOf(
        Regex("\\bdu\\b", RegexOption.IGNORE_CASE),
        Regex("\\bdich\\b", RegexOption.IGNORE_CASE),
        Regex("\\bdir\\b", RegexOption.IGNORE_CASE),
        Regex("\\bdein\\w*\\b", RegexOption.IGNORE_CASE),
        Regex("\\bhey\\b", RegexOption.IGNORE_CASE),
        Regex("\\bhi\\b", RegexOption.IGNORE_CASE),
        Regex("\\bhallo\\b", RegexOption.IGNORE_CASE),
        Regex("\\blg\\b", RegexOption.IGNORE_CASE),
        Regex("\\bliebe?r?\\b", RegexOption.IGNORE_CASE)
    )

    private val flirtySignals = listOf(
        Regex("\\b(schatz|babe|sexy|date)\\b", RegexOption.IGNORE_CASE)
    )

    private val warmSignals = listOf(
        Regex("\\bdanke(n| dir| euch)?\\b", RegexOption.IGNORE_CASE),
        Regex("\\bfreu(e|en|st|t)?\\b", RegexOption.IGNORE_CASE),
        Regex("\\bliebe?n?\\b", RegexOption.IGNORE_CASE),
        Regex("\\blg\\b", RegexOption.IGNORE_CASE),
        Regex("\\bgr(?:ue|\\u00fc)(?:\\u00df|ss)e?\\b", RegexOption.IGNORE_CASE)
    )

    fun infer(text: String): InferredStyle {
        val normalized = text.trim()
        if (normalized.isBlank()) {
            return InferredStyle(null, null)
        }

        val hasFormal = formalSignals.any { it.containsMatchIn(normalized) }
        val hasInformal = informalSignals.any { it.containsMatchIn(normalized) }

        val formality = when {
            hasFormal && !hasInformal -> Formality.SIE
            hasInformal && !hasFormal -> Formality.DU
            else -> null
        }

        val isFlirty = flirtySignals.any { it.containsMatchIn(normalized) }
        val isWarm = warmSignals.any { it.containsMatchIn(normalized) }

        val tone = when {
            isFlirty -> Tone.FLIRTY
            isWarm -> Tone.HERZLICH
            formality == Formality.SIE -> Tone.NEUTRAL
            formality == Formality.DU -> Tone.FREUNDLICH
            else -> null
        }

        return InferredStyle(tone, formality)
    }
}
