package com.bihnerdranch.example.messenger

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(internal val messages: MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat, parent, false)
        context = parent.context
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        when (message.position) {
            Message.Position.LEFT -> {
                holder.messageTextViewLeft.text = message.text
                holder.messageTextViewRight.text = ""
                holder.messageTextViewRight.setBackgroundColor(ContextCompat.getColor(context, androidx.cardview.R.color.cardview_dark_background))
            }
            Message.Position.RIGHT -> {
                holder.messageTextViewLeft.text = ""
                holder.messageTextViewLeft.setBackgroundColor(ContextCompat.getColor(context, androidx.cardview.R.color.cardview_dark_background))
                holder.messageTextViewRight.text = message.text
            }
        }

    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextViewLeft: TextView = view.findViewById<TextView>(R.id.messages_textview_left)
        val messageTextViewRight: TextView = view.findViewById<TextView>(R.id.messages_textview_right)


    }
}

