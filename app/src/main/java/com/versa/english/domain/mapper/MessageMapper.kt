package com.versa.english.domain.mapper

import com.versa.english.domain.model.MessageDomain
import com.versa.english.presentation.model.MessageStatus
import com.versa.english.presentation.model.MessageUi

fun MessageDomain.toUi(): MessageUi = MessageUi(text = this.content, isUser = this.isUser)