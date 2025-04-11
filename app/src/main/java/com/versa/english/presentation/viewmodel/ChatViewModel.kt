package com.versa.english.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.versa.english.domain.model.ChatConfig
import com.versa.english.domain.model.MessageDomain
import com.versa.english.data.repository.ChatRepositoryImpl
import com.versa.english.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.launch

sealed class MessageStatus {
    object Sending : MessageStatus()
    object Sent : MessageStatus()
    data class Error(val message: String) : MessageStatus()
}

class ChatViewModel(private val useCase: SendMessageUseCase) : ViewModel() {
    private val _messages = MutableLiveData<List<MessageDomain>>()
    val messages: LiveData<List<MessageDomain>> = _messages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _messageStatus = MutableLiveData<MessageStatus>()
    val messageStatus: LiveData<MessageStatus> = _messageStatus

    private var currentConfig = ChatConfig()

    fun updateConfig(config: ChatConfig) {
        currentConfig = config
    }

    private var count = 0

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        // Add user message immediately
        val userMessage = MessageDomain(message, true)
        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        currentMessages.add(userMessage)
        _messages.value = currentMessages
        _messageStatus.value = MessageStatus.Sending

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val assistantMessage = useCase.invoke(message, currentConfig)
                val updatedMessages = _messages.value?.toMutableList() ?: mutableListOf()
                updatedMessages.add(assistantMessage)
                _messages.value = updatedMessages
                _messageStatus.value = MessageStatus.Sent
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
                _messageStatus.value = MessageStatus.Error(e.message ?: "An error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }
} 