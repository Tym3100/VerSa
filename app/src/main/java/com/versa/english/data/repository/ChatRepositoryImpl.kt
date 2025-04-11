package com.versa.english.data.repository

import android.util.Log
import com.versa.english.data.api.ApiErrorHandler
import com.versa.english.api.ChatGPTService
import com.versa.english.api.ChatRequest
import com.versa.english.domain.model.ChatConfig
import com.versa.english.domain.model.LanguageLevel
import com.versa.english.domain.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "ChatRepository"

class ChatRepositoryImpl(private val chatGPTService: ChatGPTService) {
    private val messages = mutableListOf<Message>()

    suspend fun sendMessage(
        userMessage: String,
        config: ChatConfig
    ): Message = withContext(Dispatchers.IO) {
        val systemPrompt = buildSystemPrompt(config)
        Log.d(TAG, "Sending message: $userMessage")
        val apiMessages = buildApiMessages(systemPrompt, userMessage)

        val response = ApiErrorHandler.withRetry {
            chatGPTService.sendMessage(
                ChatRequest(
                    messages = apiMessages,
                    max_tokens = getMaxTokensForLevel(config.languageLevel)
                )
            )
        }

        Log.d(TAG, "Received response: ${response.choices.first().message.content}")

        Message(
            content = response.choices.first().message.content,
            isUser = false
        )
    }

    private fun getMaxTokensForLevel(level: LanguageLevel): Int {
        return when (level) {
            LanguageLevel.BEGINNER -> 500
            LanguageLevel.INTERMEDIATE -> 750
            LanguageLevel.ADVANCED -> 1000
            LanguageLevel.FLUENT -> 1500
        }
    }

    private fun buildSystemPrompt(config: ChatConfig): String {
        return """
            You are an English language practice partner. 
            User's language level: ${config.languageLevel.name.lowercase()}
            Communication tone: ${config.communicationTone.name.lowercase()}
            Response style: ${config.responseStyle.name.lowercase()}
            ${if (config.topic.isNotEmpty()) "Current topic: ${config.topic}" else ""}
        """.trimIndent()
    }

    private fun buildApiMessages(
        systemPrompt: String,
        userMessage: String
    ): List<com.versa.english.api.Message> {
        return listOf(
            com.versa.english.api.Message("system", systemPrompt),
            com.versa.english.api.Message("user", userMessage)
        )
    }
}