package com.versa.english.data.model.request_models

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val model: String = "deepseek-chat",
    val messages: List<ApiMessage>,
    val temperature: Double = 0.7,
    @SerializedName("max_tokens") val maxTokens: Int = 1000,
    val stream: Boolean = false
)