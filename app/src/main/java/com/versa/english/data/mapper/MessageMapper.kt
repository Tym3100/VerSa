package com.versa.english.data.mapper

import com.versa.english.data.model.request_models.ApiMessage
import com.versa.english.data.model.response_models.MessageResponse
import com.versa.english.domain.model.MessageDomain

fun MessageResponse.toDomain(): MessageDomain = MessageDomain(this.content, this.isUser)

fun MessageResponse.toData(): ApiMessage =
    ApiMessage(this.content, if (this.isUser) "user" else "assistant")