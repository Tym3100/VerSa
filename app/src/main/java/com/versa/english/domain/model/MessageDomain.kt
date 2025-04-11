package com.versa.english.domain.model

data class MessageDomain(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) 