package com.versa.english.data.model.request_models

import com.versa.english.domain.model.ChatConfig
import com.versa.english.domain.model.LanguageLevel

fun buildSystemPrompt(config: ChatConfig): String {
    return """
        You are an English language practice partner. 
        User's language level: ${config.languageLevel.name.lowercase()}
        Communication tone: ${config.communicationTone.name.lowercase()}
        Response style: ${config.responseStyle.name.lowercase()}
        ${if (config.topic.isNotEmpty()) "Current topic: ${config.topic}" else ""}
    """.trimIndent()
}

fun buildApiMessages(
    systemPrompt: String,
    userMessage: String,
    messagesHistory: MutableList<ApiMessage>
): List<ApiMessage> {
    messagesHistory.addAll(
        listOf(
            ApiMessage("system", systemPrompt),
            ApiMessage("user", userMessage)
        )
    )
    return messagesHistory
}

fun getMaxTokensForLevel(level: LanguageLevel): Int {
    return when (level) {
        LanguageLevel.BEGINNER -> 500
        LanguageLevel.INTERMEDIATE -> 750
        LanguageLevel.ADVANCED -> 1000
        LanguageLevel.FLUENT -> 1500
    }
}