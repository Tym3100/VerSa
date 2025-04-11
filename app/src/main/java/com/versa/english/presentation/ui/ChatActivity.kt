package com.versa.english.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.versa.english.presentation.adapter.MessageAdapter
import com.versa.english.data.api.ApiConfig
import com.versa.english.api.ChatGPTService
import com.versa.english.databinding.ActivityChatBinding
import com.versa.english.domain.model.ChatConfig
import com.versa.english.data.repository.ChatRepositoryImpl
import com.versa.english.presentation.viewmodel.ChatViewModel
import com.versa.english.presentation.viewmodel.ChatViewModelFactory
import com.versa.english.presentation.viewmodel.MessageStatus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var messageAdapter: MessageAdapter
    private var lastUserMessagePosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val config = intent.getSerializableExtra("chat_config") as ChatConfig
        Log.i(this.javaClass.simpleName, "config=$config")
        setupViewModel(config)
        setupRecyclerView()
        setupSendButton()
        observeViewModel()
    }

    private fun setupViewModel(config: ChatConfig) {
        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val chatGPTService = retrofit.create(ChatGPTService::class.java)
        val repository = ChatRepositoryImpl(chatGPTService)
        val factory = ChatViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]
        viewModel.updateConfig(config)
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true // Show messages from bottom
            }
            adapter = messageAdapter
        }
    }

    private fun setupSendButton() {
        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            if (message.isNotBlank()) {
                binding.messageEditText.text.clear()
                viewModel.sendMessage(message)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(this) { messages ->
            messageAdapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.messagesRecyclerView.scrollToPosition(messages.size - 1) // Scroll to the latest message
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.sendButton.isEnabled = !isLoading
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        viewModel.messageStatus.observe(this) { status ->
            when (status) {
                is MessageStatus.Sending -> {
                    lastUserMessagePosition = viewModel.messages.value?.size?.minus(1) ?: -1
                    if (lastUserMessagePosition != -1) {
                        messageAdapter.updateMessageStatus(lastUserMessagePosition, status)
                    }
                }
                is MessageStatus.Sent -> {
                    if (lastUserMessagePosition != -1) {
                        messageAdapter.updateMessageStatus(lastUserMessagePosition, status)
                    }
                }
                is MessageStatus.Error -> {
                    if (lastUserMessagePosition != -1) {
                        messageAdapter.updateMessageStatus(lastUserMessagePosition, status)
                    }
                    Toast.makeText(this, "Ошибка: ${status.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
} 