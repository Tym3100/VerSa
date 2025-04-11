package com.versa.english.domain.repository

import com.versa.english.domain.model.ChatConfig
import com.versa.english.domain.model.MessageDomain

interface ChatRepository {
    suspend fun sendMessage(userMessage: String, config: ChatConfig): MessageDomain
}