package com.versa.english.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.versa.english.R
import com.versa.english.presentation.model.MessageUi
import com.versa.english.presentation.viewmodel.MessageStatus

private const val VIEW_TYPE_USER = 0
private const val VIEW_TYPE_ASSISTANT = 1

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private val messages = mutableListOf<MessageUi>()
    private val messageStatuses = mutableMapOf<Int, MessageStatus>()

    fun submitList(newMessages: List<MessageUi>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_ASSISTANT
    }

    fun updateMessageStatus(position: Int, status: MessageStatus) {
        messageStatuses[position] = status
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_user, parent, false)
                MessageViewHolder(view)
            }

            VIEW_TYPE_ASSISTANT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_assistent, parent, false)
                MessageViewHolder(view)
            }

            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position], messageStatuses[position])
    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private val messageContainer: View = itemView.findViewById(R.id.messageContainer)
        private val sendingProgressBar: ProgressBar = itemView.findViewById(R.id.sendingProgressBar)
        private val statusImageView: ImageView = itemView.findViewById(R.id.statusImageView)

        fun bind(message: MessageUi, status: MessageStatus?) {
            messageTextView.text = message.text
            val layoutParams = messageContainer.layoutParams as ViewGroup.MarginLayoutParams

            if (message.isUser) {
                layoutParams.marginStart = 64
                layoutParams.marginEnd = 16
                messageContainer.setBackgroundResource(R.drawable.user_message_background)

                // Show status indicators only for user messages
                when (status) {
                    is MessageStatus.Sending -> {
                        sendingProgressBar.visibility = View.VISIBLE
                        statusImageView.visibility = View.GONE
                    }

                    is MessageStatus.Sent -> {
                        sendingProgressBar.visibility = View.GONE
                        statusImageView.visibility = View.VISIBLE
                        statusImageView.setImageResource(R.drawable.ic_sent)
                    }

                    is MessageStatus.Error -> {
                        sendingProgressBar.visibility = View.GONE
                        statusImageView.visibility = View.VISIBLE
                        statusImageView.setImageResource(R.drawable.ic_error)
                    }

                    null -> {
                        sendingProgressBar.visibility = View.GONE
                        statusImageView.visibility = View.GONE
                    }
                }
            } else {
                layoutParams.marginStart = 16
                layoutParams.marginEnd = 64
                messageContainer.setBackgroundResource(R.drawable.assistant_message_background)
                sendingProgressBar.visibility = View.GONE
                statusImageView.visibility = View.GONE
            }

            messageContainer.layoutParams = layoutParams
        }
    }
} 