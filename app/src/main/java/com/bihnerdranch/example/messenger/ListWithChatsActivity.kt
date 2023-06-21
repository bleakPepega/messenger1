package com.bihnerdranch.example.messenger

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ListWithChatsActivity : AppCompatActivity(), ChatsAdapter.OnItemClickListener {
    private lateinit var button: Button
    private lateinit var editText: EditText
    private lateinit var adapter: ChatsAdapter
    private lateinit var button2: Button
    private lateinit var button3: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_with_chats)
        button = findViewById(R.id.button)
        button2 = findViewById(R.id.button2)
        editText = findViewById(R.id.add_id_in_db)
        button3 = findViewById(R.id.test)
        val chatRecycler = findViewById<RecyclerView>(R.id.chats)
        val layoutManager = LinearLayoutManager(this)
        adapter = ChatsAdapter(mutableListOf(), this)
        chatRecycler.adapter = adapter
        chatRecycler.layoutManager = layoutManager
        DataBaseForChats(this).readValue().forEach { adapter.messages.add(it) }
        button.setOnClickListener {
            val text = editText.text.toString()
            DataBaseForChats(this).insertValue(text)
            adapter.messages.add(text)
            editText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
        button2.setOnClickListener {
            val sharedPreferences = getSharedPreferences("id", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("id")
            editor.apply()
            val intent = Intent(this@ListWithChatsActivity, EnterIdActivity::class.java)
            startActivity(intent)
        }
        editText.setOnFocusChangeListener {_, hasFocus ->
            when(hasFocus){
                true -> editText.setText("")
                false -> editText.setText("enter id нового пользователя")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(text: String) {
        // Обработка нажатия на элемент здесь
        val intent = Intent(this@ListWithChatsActivity, ChatActivity::class.java)
        intent.putExtra("key", text)
        AppealToDataBase(this).searchKeyInTable(text)
        startActivity(intent)
        Toast.makeText(this, "Item clicked: $text", Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClick(view: View, text: String, position: Int) {
        val popMenu = PopupMenu(this, view)
        popMenu.inflate(R.menu.chat_item_menu)
        popMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_delete_chat -> {
                    DataBaseForChats(this).deleteValue(text)
                    adapter.messages.clear()
                    DataBaseForChats(this).readValue().forEach { adapter.messages.add(it) }
                    adapter.notifyItemRemoved(position)
                    true
                }
                R.id.action_edit -> {
                    Toast.makeText(this, "Реализую потом", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popMenu.show()
    }
}