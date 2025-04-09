package com.versa.english.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.versa.english.databinding.ActivityConfigBinding
import com.versa.english.model.ChatConfig
import com.versa.english.model.CommunicationTone
import com.versa.english.model.LanguageLevel
import com.versa.english.model.ResponseStyle

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
        val languageLevel = when (binding.languageLevelGroup.checkedRadioButtonId) {
            binding.beginnerLevel.id -> LanguageLevel.BEGINNER
            binding.intermediateLevel.id -> LanguageLevel.INTERMEDIATE
            binding.advancedLevel.id -> LanguageLevel.ADVANCED
            binding.fluentLevel.id -> LanguageLevel.FLUENT
            else -> LanguageLevel.INTERMEDIATE
        }

        val communicationTone = when (binding.communicationToneGroup.checkedRadioButtonId) {
            binding.formalTone.id -> CommunicationTone.FORMAL
            binding.informalTone.id -> CommunicationTone.INFORMAL
            else -> CommunicationTone.FORMAL
        }

        val responseStyle = when (binding.responseStyleGroup.checkedRadioButtonId) {
            binding.shortStyle.id -> ResponseStyle.SHORT
            binding.mediumStyle.id -> ResponseStyle.MEDIUM
            binding.longStyle.id -> ResponseStyle.LONG
            binding.customStyle.id -> ResponseStyle.CUSTOM
            else -> ResponseStyle.MEDIUM
        }

        return ChatConfig(
            languageLevel = languageLevel,
            communicationTone = communicationTone,
            responseStyle = responseStyle,
            personalization = binding.personalizationCheckbox.isChecked,
            topic = binding.topicEditText.text.toString()
        )
    }

    private fun startChatActivity(config: ChatConfig) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("chat_config", config)
        }
        startActivity(intent)
    }
} 