package com.versa.english.data.model.response_models

data class ChatResponse(
    val id: String,
    val choices: List<Choice>,
    val created: Long,
    val model: String,
    val usage: Usage
)