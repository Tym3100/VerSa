package com.versa.english.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.versa.english.databinding.ActivityConfigBinding
import com.versa.english.domain.model.ChatConfig
import com.versa.english.domain.model.CommunicationTone
import com.versa.english.domain.model.LanguageLevel
import com.versa.english.domain.model.ResponseStyle

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startConversationButton.setOnClickListener {
            val config = createConfigFromUI()
            startChatActivity(config)
        }
    }

    private fun createConfigFromUI(): ChatConfig {
        val languageLevel = when (binding.languageLevelGroup.checkedChipId) {
            binding.beginnerChip.id -> LanguageLevel.BEGINNER
            binding.intermediateChip.id -> LanguageLevel.INTERMEDIATE
            binding.advancedChip.id -> LanguageLevel.ADVANCED
            binding.fluentChip.id -> LanguageLevel.FLUENT
            else -> LanguageLevel.INTERMEDIATE
        }

        val communicationTone = when (binding.communicationToneGroup.checkedChipId) {
            binding.formalChip.id -> CommunicationTone.FORMAL
            binding.informalChip.id -> CommunicationTone.INFORMAL
            else -> CommunicationTone.FORMAL
        }

        val responseStyle = when (binding.responseStyleGroup.checkedChipId) {
            binding.shortChip.id -> ResponseStyle.SHORT
            binding.mediumChip.id -> ResponseStyle.MEDIUM
            binding.longChip.id -> ResponseStyle.LONG
            else -> ResponseStyle.MEDIUM
        }

        return ChatConfig(
            languageLevel = languageLevel,
            communicationTone = communicationTone,
            responseStyle = responseStyle,
            topic = binding.topicInput.text.toString()
        )
    }

    private fun startChatActivity(config: ChatConfig) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("chat_config", config)
        }
        startActivity(intent)
    }
} 