package com.versa.english.data.mapper

import com.versa.english.data.model.response_models.MessageResponse
import com.versa.english.domain.model.MessageDomain

fun MessageResponse.toDomain(): MessageDomain = MessageDomain(this.content, this.isUser)