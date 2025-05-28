package com.versa.english.presentation.ui

import DeepSeekService
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.versa.english.data.api.DeepSeekApiConfig
import com.versa.english.data.repository.ChatRepositoryImpl
import com.versa.english.databinding.ActivityChatBinding
import com.versa.english.domain.model.ChatConfig
import com.versa.english.domain.usecase.SendMessageUseCase
import com.versa.english.presentation.adapter.MessageAdapter
import com.versa.english.presentation.viewmodel.ChatViewModel
import com.versa.english.presentation.viewmodel.ChatViewModelFactory
import com.versa.english.presentation.viewmodel.MessageStatus
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


private const val TIMEOUT_MILLIS = 120_000L

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
        setupViewModel(config)
        setupRecyclerView()
        setupSendButton()
        observeViewModel()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_MILLIS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_MILLIS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_MILLIS, TimeUnit.SECONDS)
            .build()
    }

    private fun setupViewModel(config: ChatConfig) {

        val retrofit = Retrofit.Builder()
            .baseUrl(DeepSeekApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val deepSeekService = retrofit.create(DeepSeekService::class.java)
        val repository = ChatRepositoryImpl(deepSeekService)
        val useCase = SendMessageUseCase(repository)
        val factory = ChatViewModelFactory(useCase)
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
            if (messages.isNotEmpty())
                binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.sendButton.isVisible = !isLoading
            binding.sendingProgressBar.isVisible = isLoading
        }
        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
        viewModel.messageStatus.observe(this) { status ->
            when (status) {
                is MessageStatus.Sending -> {
                    lastUserMessagePosition = viewModel.messages.value?.size?.minus(1) ?: -1
                    if (lastUserMessagePosition != -1)
                        messageAdapter.updateMessageStatus(lastUserMessagePosition, status)
                }

                is MessageStatus.Sent -> if (lastUserMessagePosition != -1)
                    messageAdapter.updateMessageStatus(lastUserMessagePosition, status)

                is MessageStatus.Error -> {
                    if (lastUserMessagePosition != -1)
                        messageAdapter.updateMessageStatus(lastUserMessagePosition, status)
                    Toast.makeText(this, "Ошибка: ${status.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
} 