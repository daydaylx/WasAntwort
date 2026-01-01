package de.grunert.wasantwort.domain

import kotlinx.serialization.Serializable

@Serializable
data class ConversationEntry(
    val id: String,
    val timestamp: Long,
    val inputText: String,
    val tone: Tone,
    val goal: Goal,
    val length: Length,
    val emojiLevel: EmojiLevel,
    val formality: Formality,
    val suggestions: List<String>
)

@Serializable
data class ConversationContext(
    val messages: List<ContextMessage>
)

@Serializable
data class ContextMessage(
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long
)
