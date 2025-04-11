package com.versa.english.data.repository

import ChatGPTService
import android.util.Log
import com.versa.english.data.api.ApiErrorHandler
import com.versa.english.data.mapper.toDomain
import com.versa.english.data.model.request_models.ChatRequest
import com.versa.english.data.model.request_models.buildApiMessages
import com.versa.english.data.model.request_models.buildSystemPrompt
import com.versa.english.data.model.request_models.getMaxTokensForLevel
import com.versa.english.domain.model.ChatConfig
import com.versa.english.domain.model.MessageDomain
import com.versa.english.domain.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "ChatRepository"

class ChatRepositoryImpl(private val chatGPTService: ChatGPTService) : ChatRepository {
    override suspend fun sendMessage(
        userMessage: String,
        config: ChatConfig
    ): MessageDomain = withContext(Dispatchers.IO) {
        val systemPrompt = buildSystemPrompt(config)
        val apiMessages = buildApiMessages(systemPrompt, userMessage)

        Log.d(TAG, "Sending message: $apiMessages")
        val response = ApiErrorHandler.withRetry {
            chatGPTService.sendMessage(
                ChatRequest(
                    messages = apiMessages,
                    maxTokens = getMaxTokensForLevel(config.languageLevel)
                )
            )
        }
        Log.d(TAG, "Received response: ${response.choices.first().message.content}")
        response.choices.first().message.toDomain()
    }
}