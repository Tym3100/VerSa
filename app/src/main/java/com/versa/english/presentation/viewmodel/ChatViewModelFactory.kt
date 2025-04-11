package com.versa.english.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.versa.english.data.repository.ChatRepositoryImpl
import com.versa.english.domain.usecase.SendMessageUseCase

class ChatViewModelFactory(private val useCase: SendMessageUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 