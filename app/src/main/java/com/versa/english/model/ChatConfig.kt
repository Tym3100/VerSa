package com.versa.english.model

import java.io.Serializable

data class ChatConfig(
    val languageLevel: LanguageLevel = LanguageLevel.INTERMEDIATE,
    val communicationTone: CommunicationTone = CommunicationTone.FORMAL,
    val responseStyle: ResponseStyle = ResponseStyle.MEDIUM,
    val personalization: Boolean = false,
    val topic: String = ""
) : Serializable

enum class LanguageLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    FLUENT
}

enum class CommunicationTone {
    FORMAL,
    INFORMAL
}

enum class ResponseStyle {
    SHORT,
    MEDIUM,
    LONG,
    CUSTOM
} 