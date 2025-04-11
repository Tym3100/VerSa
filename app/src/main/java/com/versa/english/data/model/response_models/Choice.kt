package com.versa.english.data.model.response_models

import com.google.gson.annotations.SerializedName

data class Choice(
    val message: MessageResponse,
    @SerializedName("finish_reason") val finishReason: String,
    val index: Int
)