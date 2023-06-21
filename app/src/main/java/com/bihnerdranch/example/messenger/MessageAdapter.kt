package com.bihnerdranch.example.messenger

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter( val messages: MutableList<Message>, val listener: OnItemClickListerForMessages) : RecyclerView.Adapter<MessageAdapter.MessageApapterViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageApapterViewHolder {
        val layoutId = when (viewType) {
            VIEW_TYPE_RECEIVED_FILE -> R.layout.left_imageview
            VIEW_TYPE_SEND_FILE -> R.layout.right_image_view
            VIEW_TYPE_TEXT -> R.layout.chat
            else -> throw Exception("not cool, brother")
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        context = parent.context
        return MessageApapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageApapterViewHolder, position: Int) {

        val message = messages[position]
        if (message.filePath == null) {
            when (message.position) {
                Message.Position.LEFT -> {
                    holder.messageTextViewLeft.text = message.text
                    holder.messageTextViewRight.text = ""
                    holder.messageTextViewRight.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            androidx.cardview.R.color.cardview_dark_background
                        )
                    )
                }

                Message.Position.RIGHT -> {
                    holder.messageTextViewLeft.text = ""
                    holder.messageTextViewLeft.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            androidx.cardview.R.color.cardview_dark_background
                        )
                    )
                    holder.messageTextViewRight.text = message.text
                }
            }
        }
        else {
            holder.imageView.text = message.text
        }

    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    }
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.filePath != null) {
            when (message.position) {
                Message.Position.LEFT -> VIEW_TYPE_RECEIVED_FILE
                Message.Position.RIGHT -> VIEW_TYPE_SEND_FILE
            }
        } else {
            VIEW_TYPE_TEXT
        }
    }
    interface OnItemClickListerForMessages {
        fun onItemCLickForMessages(byteArray: ByteArray)
    }
    inner class MessageApapterViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        lateinit var imageView: TextView
        lateinit var messageTextViewLeft: TextView
        lateinit var messageTextViewRight: TextView
        init {
            if (view.findViewById<TextView>(R.id.text_view_with_icon) != null) {
                imageView = view.findViewById(R.id.text_view_with_icon)
            }
            else {
                messageTextViewLeft = view.findViewById(R.id.messages_textview_left)
                messageTextViewRight = view.findViewById(R.id.messages_textview_right)
            }

        }
//        val file: TextView = view.findViewById(R.id.text_view_with_icon)
        init {
            view.setOnClickListener(this)

        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val message = messages[position]

                message.dataFromFile?.let { listener.onItemCLickForMessages(it) }
            }
        }
    }
    companion object {
        private const val VIEW_TYPE_RECEIVED_FILE = 1
        private const val VIEW_TYPE_SEND_FILE = 2
        private const val VIEW_TYPE_TEXT = 3
    }
}

