package com.versa.english.domain.usecase

import com.versa.english.domain.model.ChatConfig
import com.versa.english.domain.model.MessageDomain
import com.versa.english.domain.repository.ChatRepository

class SendMessageUseCase constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(content: String, chatConfig: ChatConfig): MessageDomain {
        return repository.sendMessage(content, config = chatConfig)
    }
}