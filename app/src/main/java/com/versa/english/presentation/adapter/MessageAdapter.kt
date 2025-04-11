package com.versa.english.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.versa.english.R
import com.versa.english.domain.model.MessageDomain
import com.versa.english.presentation.viewmodel.MessageStatus

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private val messages = mutableListOf<MessageDomain>()
    private val messageStatuses = mutableMapOf<Int, MessageStatus>()

    fun submitList(newMessages: List<MessageDomain>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun updateMessageStatus(position: Int, status: MessageStatus) {
        messageStatuses[position] = status
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
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

        fun bind(message: MessageDomain, status: MessageStatus?) {
            messageTextView.text = message.content
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