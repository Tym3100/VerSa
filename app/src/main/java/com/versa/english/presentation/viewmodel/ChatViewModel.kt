package com.versa.english.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.versa.english.domain.model.ChatConfig
import com.versa.english.domain.usecase.SendMessageUseCase
import com.versa.english.presentation.model.MessageUi
import kotlinx.coroutines.launch

sealed class MessageStatus {
    object Sending : MessageStatus()
    object Sent : MessageStatus()
    data class Error(val message: String) : MessageStatus()
}

private const val TAG = "ChatViewModel"

class ChatViewModel(private val useCase: SendMessageUseCase) : ViewModel() {
    private val _messages = MutableLiveData<List<MessageUi>>()
    val messages: LiveData<List<MessageUi>> = _messages

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

    fun sendMessage(message: String) {
        if (message.isBlank()) return
        val userMessage = MessageUi(message, true)
        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        currentMessages.add(userMessage)
        _messages.value = currentMessages
        _messageStatus.value = MessageStatus.Sending
        val updatedMessages = _messages.value?.toMutableList() ?: mutableListOf()
        _messages.value = updatedMessages
        _messageStatus.value = MessageStatus.Sent
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val assistantMessage = useCase.invoke(message, currentConfig)
                val updatedMessages = _messages.value?.toMutableList() ?: mutableListOf()
                updatedMessages.add(assistantMessage)
                _messages.value = updatedMessages
                _messageStatus.value = MessageStatus.Sent
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.toString()}. Message error: ${e.message}")
                _error.value = e.message ?: "An error occurred"
                _messageStatus.value = MessageStatus.Error(e.message ?: "An error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }
} 