package com.bihnerdranch.example.messenger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatsAdapter(internal val messages: MutableList<String>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ChatsAdapter.ChatsAdapterViewHolde>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsAdapterViewHolde {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_with_chats, parent, false)
        return ChatsAdapterViewHolde(view)
    }

    override fun onBindViewHolder(holder: ChatsAdapterViewHolde, position: Int) {
        val message = messages[position]
        holder.chat.text = message

    }
    interface OnItemClickListener {
        fun onItemClick(text: String)
        fun onItemLongClick(view: View, text: String, position: Int)
    }

    override fun getItemCount() = messages.size

    inner class ChatsAdapterViewHolde(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
        val chat: TextView = view.findViewById(R.id.chat)

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val message = messages[position]
                listener.onItemClick(message)
            }
        }
        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val message = messages[position]
                listener.onItemLongClick(v!!, message, position)
            }
            return true
        }
    }
}