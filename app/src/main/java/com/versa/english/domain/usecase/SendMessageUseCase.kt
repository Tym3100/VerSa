package com.versa.english.domain.usecase

import com.versa.english.domain.mapper.toUi
import com.versa.english.domain.model.ChatConfig
import com.versa.english.domain.repository.ChatRepository
import com.versa.english.presentation.model.MessageUi

class SendMessageUseCase constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(content: String, chatConfig: ChatConfig): MessageUi {
        return repository.sendMessage(content, config = chatConfig).toUi()
    }
}